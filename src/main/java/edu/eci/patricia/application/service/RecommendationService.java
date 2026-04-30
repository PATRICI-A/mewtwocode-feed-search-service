package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ScoredPatch;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService implements FeedUseCase {

    private static final int MAX_RECOMMENDATIONS = 10;
    private static final float INTEREST_TAG_SCORE   = 0.40f;
    private static final float JOIN_SCORE_PER_HIT   = 0.15f;
    private static final float JOIN_SCORE_CAP       = 0.30f;
    private static final float VIEW_SCORE_PER_HIT   = 0.05f;
    private static final float VIEW_SCORE_CAP       = 0.10f;
    private static final float SKIP_PENALTY_PER_HIT = 0.10f;
    private static final float SKIP_PENALTY_CAP     = 0.20f;

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

        // 1. Tags de interés del usuario en mayúsculas para comparar con el enum
        Set<String> interestTags = interestRepository.findByUserId(userId).stream()
                .map(UserInterest::getInterestingTag)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // 2. Historial de interacciones del usuario
        List<FeedInteraction> interactions = interactionRepository.findByUserId(userId);
        Set<UUID> alreadyInteracted = interactionRepository.findInteractedPatchIds(userId);

        // 3. Mapa patchId → categoría para saber la categoría de cada interacción pasada
        Map<UUID, PatchCategory> categoryByPatchId = buildCategoryLookup(alreadyInteracted);

        // 4. Conteo de interacciones por categoría
        Map<PatchCategory, Long> joinsByCategory = countByCategory(interactions, InteractionType.JOIN, categoryByPatchId);
        Map<PatchCategory, Long> viewsByCategory = countByCategory(interactions, InteractionType.VIEW, categoryByPatchId);
        Map<PatchCategory, Long> skipsByCategory = countByCategory(interactions, InteractionType.SKIP, categoryByPatchId);

        // 5. Candidatos: patches abiertos, públicos, que el usuario no haya visto
        List<Patch> candidates = patchRepository.findOpenPublicPatches().stream()
                .filter(p -> !alreadyInteracted.contains(p.getId()))
                .toList();

        // 6. Puntuar, filtrar y ordenar
        return candidates.stream()
                .map(p -> score(p, interestTags, joinsByCategory, viewsByCategory, skipsByCategory))
                .filter(sp -> sp.getAffinityScore() > 0)
                .sorted(Comparator.comparing(ScoredPatch::getAffinityScore).reversed())
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    // Carga las categorías de los patches con los que el usuario interactuó (evita N+1)
    private Map<UUID, PatchCategory> buildCategoryLookup(Set<UUID> patchIds) {
        if (patchIds.isEmpty()) return Collections.emptyMap();
        return patchRepository.findByIds(new ArrayList<>(patchIds)).stream()
                .collect(Collectors.toMap(Patch::getId, Patch::getCategory));
    }

    // Aplica el algoritmo de scoring a un patch candidato
    private ScoredPatch score(Patch patch,
                               Set<String> interestTags,
                               Map<PatchCategory, Long> joinsByCategory,
                               Map<PatchCategory, Long> viewsByCategory,
                               Map<PatchCategory, Long> skipsByCategory) {
        float score = 0f;
        List<String> reasons = new ArrayList<>();
        PatchCategory category = patch.getCategory();

        // Señal 1: el tag de interés del usuario coincide con la categoría
        if (interestTags.contains(category.name())) {
            score += INTEREST_TAG_SCORE;
            reasons.add("matches your interests");
        }

        // Señal 2: el usuario ha hecho JOIN en esta categoría antes
        long joins = joinsByCategory.getOrDefault(category, 0L);
        if (joins > 0) {
            score += Math.min(joins * JOIN_SCORE_PER_HIT, JOIN_SCORE_CAP);
            reasons.add("similar to patches you've joined");
        }

        // Señal 3: el usuario ha visto patches de esta categoría
        long views = viewsByCategory.getOrDefault(category, 0L);
        if (views > 0) {
            score += Math.min(views * VIEW_SCORE_PER_HIT, VIEW_SCORE_CAP);
        }

        // Penalización: el usuario ha skipeado esta categoría
        long skips = skipsByCategory.getOrDefault(category, 0L);
        if (skips > 0) {
            score -= Math.min(skips * SKIP_PENALTY_PER_HIT, SKIP_PENALTY_CAP);
        }

        String reason = reasons.isEmpty()
                ? "Popular in your campus zone"
                : String.join(" and ", reasons);

        return ScoredPatch.builder()
                .patchId(patch.getId())
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
