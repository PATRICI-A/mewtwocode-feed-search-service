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

/**
 * Application service that computes personalised patch recommendations for a given user.
 *
 * <p>When the user has at least one recorded category score the service scores every open public
 * patch by affinity (category score / {@code MAX_CAT_SCORE}, capped at 1.0), filters out patches
 * the user has already joined, and returns the top {@code MAX_RECOMMENDATIONS} results sorted by
 * descending affinity score.
 *
 * <p>When no category scores exist (new user), the service falls back to a popularity-based list
 * of patches, assigning each a fixed {@code POPULAR_FALLBACK_SCORE} of 0.30.
 */
@Service
public class RecommendationService implements GetRecommendationsPort {

    private static final int   MAX_RECOMMENDATIONS  = 10;
    private static final float MAX_CAT_SCORE        = 100f;
    private static final float POPULAR_FALLBACK_SCORE = 0.30f;

    private final PatchRepositoryPort patchRepository;
    private final UserCategoryScoreRepositoryPort categoryScoreRepository;
    private final FeedInteractionRepositoryPort interactionRepository;

    /**
     * Constructs a {@code RecommendationService} with all required collaborators.
     *
     * @param patchRepository         port for reading patch data
     * @param categoryScoreRepository port for reading per-user category scores
     * @param interactionRepository   port for reading past user interactions (joined patch IDs)
     */
    public RecommendationService(PatchRepositoryPort patchRepository,
                                 UserCategoryScoreRepositoryPort categoryScoreRepository,
                                 FeedInteractionRepositoryPort interactionRepository) {
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
        this.interactionRepository = interactionRepository;
    }

    /**
     * Returns a ranked list of patch recommendations personalised for the given user.
     * Patches the user has already joined are excluded from the results.
     * Falls back to popular patches when the user has no recorded interests.
     *
     * @param userId the identifier of the user requesting recommendations
     * @return an ordered list of up to {@code MAX_RECOMMENDATIONS} {@link ScoredPatch} objects
     *         sorted by descending affinity score
     */
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

    /**
     * Produces a popularity-based fallback recommendation list for users with no category history.
     * Each returned patch receives the fixed {@code POPULAR_FALLBACK_SCORE}.
     *
     * @param joinedPatchIds set of patch IDs the user has already joined; these are excluded
     * @return an ordered list of up to {@code MAX_RECOMMENDATIONS} popular {@link ScoredPatch} objects
     */
    private List<ScoredPatch> popularFallback(Set<UUID> joinedPatchIds) {
        return patchRepository.findPopularPatches(MAX_RECOMMENDATIONS * 2).stream()
                .filter(p -> !joinedPatchIds.contains(p.getId()))
                .map(p -> ScoredPatch.builder()
                        .patch(p)
                        .affinityScore(POPULAR_FALLBACK_SCORE)
                        .reason("Popular on campus")
                        .build())
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    /**
     * Computes the affinity score for a single patch relative to the user's category interests.
     *
     * @param patch          the candidate patch to score
     * @param categoryScores map of category to accumulated user score (0–{@code MAX_CAT_SCORE})
     * @return a {@link ScoredPatch} with an affinity value in the range [0.0, 1.0]
     */
    private ScoredPatch score(Patch patch, Map<PatchCategory, Float> categoryScores) {
        float catScore = categoryScores.getOrDefault(patch.getCategory(), 0f);
        float affinity = catScore > 0 ? Math.min(catScore / MAX_CAT_SCORE, 1.0f) : 0f;

        return ScoredPatch.builder()
                .patch(patch)
                .affinityScore(affinity)
                .reason("matches your profile")
                .build();
    }
}
