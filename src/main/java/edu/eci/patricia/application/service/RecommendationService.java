package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.GetRecommendationsPort;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ScoredPatch;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService implements GetRecommendationsPort {

    private static final int MAX_RECOMMENDATIONS = 10;
    private static final float INTEREST_TAG_SCORE   = 0.40f;
    private static final float JOIN_SCORE_PER_HIT   = 0.15f;
    private static final float JOIN_SCORE_CAP        = 0.30f;
    private static final float VIEW_SCORE_PER_HIT   = 0.05f;
    private static final float VIEW_SCORE_CAP        = 0.10f;
    private static final float SKIP_PENALTY_PER_HIT = 0.10f;
    private static final float SKIP_PENALTY_CAP      = 0.20f;
    private static final float POPULAR_FALLBACK_SCORE  = 0.30f;

    private final PatchRepositoryPort patchRepository;
    private final UserInterestRepositoryPort interestRepository;
    private final FeedInteractionRepositoryPort interactionRepository;

    public RecommendationService(PatchRepositoryPort patchRepository,
                                 UserInterestRepositoryPort interestRepository,
                                 FeedInteractionRepositoryPort interactionRepository) {
        this.patchRepository = patchRepository;
        this.interestRepository = interestRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public List<ScoredPatch> getRecommendations(UUID userId) {

        // 1. Tags de interés del usuario
        List<UserInterest> interests = interestRepository.findByUserId(userId);
        Set<String> interestTags = interests.stream()
                .map(UserInterest::getInterestingTag)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // 2. Historial de interacciones y patches donde ya hizo JOIN
        List<FeedInteraction> interactions = interactionRepository.findByUserId(userId);
        Set<UUID> joinedPatchIds = interactionRepository.findJoinedPatchIds(userId);

        // 3. Usuario nuevo: sin intereses ni historial → fallback popular
        boolean isNewUser = interests.isEmpty() && interactions.isEmpty();
        if (isNewUser) {
            return popularFallback(joinedPatchIds);
        }

        // 4. Mapa patchId → categoría para contar interacciones por categoría
        Map<UUID, PatchCategory> categoryByPatchId = buildCategoryLookup(
                interactions.stream().map(FeedInteraction::getPatchId).collect(Collectors.toSet())
        );

        // 5. Conteo de interacciones por categoría y tipo
        Map<PatchCategory, Long> joinsByCategory = countByCategory(interactions, InteractionType.JOIN, categoryByPatchId);
        Map<PatchCategory, Long> viewsByCategory = countByCategory(interactions, InteractionType.VIEW, categoryByPatchId);
        Map<PatchCategory, Long> skipsByCategory = countByCategory(interactions, InteractionType.SKIP, categoryByPatchId);

        // 6. Candidatos: patches abiertos y públicos donde el usuario no hizo JOIN
        //    VIEW y SKIP no excluyen el parche — solo influyen en el score
        List<Patch> candidates = patchRepository.findOpenPublicPatches().stream()
                .filter(p -> !joinedPatchIds.contains(p.getId()))
                .toList();

        // 7. Puntuar, filtrar score > 0 y ordenar descendentemente
        return candidates.stream()
                .map(p -> score(p, interestTags, joinsByCategory, viewsByCategory, skipsByCategory))
                .filter(sp -> sp.getAffinityScore() > 0)
                .sorted(Comparator.comparing(ScoredPatch::getAffinityScore).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    private List<ScoredPatch> popularFallback(Set<UUID> joinedPatchIds) {
        return patchRepository.findPopularPatches(MAX_RECOMMENDATIONS * 2).stream()
                .filter(p -> !joinedPatchIds.contains(p.getId()))
                .map(p -> ScoredPatch.builder()
                        .patch(p)
                        .affinityScore(POPULAR_FALLBACK_SCORE)
                        .reason("Popular en tu campus")
                        .build())
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    private Map<UUID, PatchCategory> buildCategoryLookup(Set<UUID> patchIds) {
        if (patchIds.isEmpty()) return Collections.emptyMap();
        return patchRepository.findByIds(new ArrayList<>(patchIds)).stream()
                .collect(Collectors.toMap(Patch::getId, Patch::getCategory));
    }

    private ScoredPatch score(Patch patch,
                               Set<String> interestTags,
                               Map<PatchCategory, Long> joinsByCategory,
                               Map<PatchCategory, Long> viewsByCategory,
                               Map<PatchCategory, Long> skipsByCategory) {
        float score = 0f;
        List<String> reasons = new ArrayList<>();
        PatchCategory category = patch.getCategory();

        if (interestTags.contains(category.name())) {
            score += INTEREST_TAG_SCORE;
            reasons.add("coincide con tus intereses");
        }

        long joins = joinsByCategory.getOrDefault(category, 0L);
        if (joins > 0) {
            score += Math.min(joins * JOIN_SCORE_PER_HIT, JOIN_SCORE_CAP);
            reasons.add("similar a parches en los que has participado");
        }

        long views = viewsByCategory.getOrDefault(category, 0L);
        if (views > 0) {
            score += Math.min(views * VIEW_SCORE_PER_HIT, VIEW_SCORE_CAP);
        }

        long skips = skipsByCategory.getOrDefault(category, 0L);
        if (skips > 0) {
            score -= Math.min(skips * SKIP_PENALTY_PER_HIT, SKIP_PENALTY_CAP);
        }

        String reason = reasons.isEmpty()
                ? "Popular en tu campus"
                : String.join(" y ", reasons);

        return ScoredPatch.builder()
                .patch(patch)
                .affinityScore(score)
                .reason(reason)
                .build();
    }

    private Map<PatchCategory, Long> countByCategory(List<FeedInteraction> interactions,
                                                      InteractionType type,
                                                      Map<UUID, PatchCategory> categoryByPatchId) {
        return interactions.stream()
                .filter(i -> i.getAction() == type)
                .collect(Collectors.groupingBy(
                        i -> categoryByPatchId.getOrDefault(i.getPatchId(), PatchCategory.OTHER),
                        Collectors.counting()
                ));
    }
}
