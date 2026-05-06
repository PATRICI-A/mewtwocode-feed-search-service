package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.GetRecommendationsPort;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ScoredPatch;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService implements GetRecommendationsPort {

    private static final int   MAX_RECOMMENDATIONS  = 10;
    private static final float MAX_CAT_SCORE        = 100f;
    private static final float POPULAR_FALLBACK_SCORE = 0.30f;

    private final PatchRepositoryPort patchRepository;
    private final UserCategoryScoreRepositoryPort categoryScoreRepository;
    private final FeedInteractionRepositoryPort interactionRepository;

    public RecommendationService(PatchRepositoryPort patchRepository,
                                 UserCategoryScoreRepositoryPort categoryScoreRepository,
                                 FeedInteractionRepositoryPort interactionRepository) {
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
        this.interactionRepository = interactionRepository;
    }

    @Override
    public List<ScoredPatch> getRecommendations(UUID userId) {
        Map<PatchCategory, Float> categoryScores = categoryScoreRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(UserCategoryScore::getCategory, UserCategoryScore::getScoreTotal));

        Set<UUID> joinedPatchIds = interactionRepository.findJoinedPatchIds(userId);

        if (categoryScores.isEmpty()) {
            return popularFallback(joinedPatchIds);
        }

        List<Patch> candidates = patchRepository.findOpenPublicPatches().stream()
                .filter(p -> !joinedPatchIds.contains(p.getId()))
                .toList();

        return candidates.stream()
                .map(p -> score(p, categoryScores))
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

    private ScoredPatch score(Patch patch, Map<PatchCategory, Float> categoryScores) {
        float catScore = categoryScores.getOrDefault(patch.getCategory(), 0f);
        float affinity = catScore > 0 ? Math.min(catScore / MAX_CAT_SCORE, 1.0f) : 0f;

        return ScoredPatch.builder()
                .patch(patch)
                .affinityScore(affinity)
                .reason("compatible con tu perfil")
                .build();
    }
}
