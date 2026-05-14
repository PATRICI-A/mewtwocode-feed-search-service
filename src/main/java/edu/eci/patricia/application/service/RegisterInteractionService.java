package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.RegisterInteractionPort;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service that records a user's interaction with a patch and updates the
 * corresponding user–category score accordingly.
 *
 * <p>Score adjustments per interaction type:
 * <ul>
 *   <li>{@code VIEW}  — +{@code WEIGHT_VIEW}  (+0.1)</li>
 *   <li>{@code JOIN}  — +{@code WEIGHT_JOIN}  (+1.0)</li>
 *   <li>{@code SKIP}  — +{@code WEIGHT_SKIP} (-10.0)</li>
 * </ul>
 *
 * <p>Before applying the event weight the existing score is decayed using the formula
 * {@code score * e^(-LAMBDA * days)} where {@code LAMBDA = 0.01}. The resulting score is
 * clamped to [0.0, {@code MAX_CAT_SCORE}].
 */
@Service
public class RegisterInteractionService implements RegisterInteractionPort {

    private static final float LAMBDA = 0.01f;
    private static final float MAX_CAT_SCORE = 100f;
    private static final float WEIGHT_VIEW  =  0.1f;
    private static final float WEIGHT_JOIN  =  1.0f;
    private static final float WEIGHT_SKIP  = -10.0f;

    private final FeedInteractionRepositoryPort interactionRepository;
    private final PatchRepositoryPort patchRepository;
    private final UserCategoryScoreRepositoryPort categoryScoreRepository;

    /**
     * Constructs a {@code RegisterInteractionService} with all required collaborators.
     *
     * @param interactionRepository   port for persisting interaction events
     * @param patchRepository         port for reading patch data (to resolve the patch category)
     * @param categoryScoreRepository port for reading and writing user–category scores
     */
    public RegisterInteractionService(FeedInteractionRepositoryPort interactionRepository,
                                      PatchRepositoryPort patchRepository,
                                      UserCategoryScoreRepositoryPort categoryScoreRepository) {
        this.interactionRepository = interactionRepository;
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
    }

    /**
     * Persists an interaction event and triggers a category-score update for the user.
     *
     * @param userId  the identifier of the user who performed the interaction
     * @param patchId the identifier of the patch that was interacted with
     * @param action  the type of interaction ({@code VIEW}, {@code JOIN}, or {@code SKIP})
     */
    @Override
    public void register(UUID userId, UUID patchId, InteractionType action) {
        interactionRepository.save(userId, patchId, action);
        updateCategoryScore(userId, patchId, action);
    }

    /**
     * Resolves the category of the interacted patch, applies exponential decay to the existing
     * score, adds the event weight, clamps the result to [0.0, {@code MAX_CAT_SCORE}], and
     * persists the updated {@link UserCategoryScore}.
     *
     * @param userId  the identifier of the user whose score is being updated
     * @param patchId the identifier of the patch to look up the category from
     * @param action  the interaction type that determines the score delta
     */
    private void updateCategoryScore(UUID userId, UUID patchId, InteractionType action) {
        List<Patch> patches = patchRepository.findByIds(List.of(patchId));
        if (patches.isEmpty()) return;

        PatchCategory category = patches.get(0).getCategory();
        float eventWeight = eventWeight(action);
        LocalDateTime now = LocalDateTime.now();

        Optional<UserCategoryScore> existing = categoryScoreRepository.findByUserIdAndCategory(userId, category);

        float decayed = existing.map(s -> applyDecay(s.getScoreTotal(), s.getLastUpdated(), now)).orElse(0f);
        float newScore = Math.max(0f, Math.min(decayed + eventWeight, MAX_CAT_SCORE));

        UserCategoryScore score = existing.map(s -> {
            s.setScoreTotal(newScore);
            s.setLastUpdated(now);
            return s;
        }).orElseGet(() -> UserCategoryScore.builder()
                .userId(userId)
                .category(category)
                .scoreTotal(newScore)
                .lastUpdated(now)
                .build());

        categoryScoreRepository.save(score);
    }

    /**
     * Applies exponential time-decay to a category score using the formula
     * {@code currentScore * e^(-LAMBDA * days)}.
     *
     * @param currentScore the score value before decay
     * @param lastUpdated  the timestamp when the score was last written
     * @param now          the current timestamp used to compute elapsed days
     * @return the decayed score; always non-negative
     */
    private float applyDecay(float currentScore, LocalDateTime lastUpdated, LocalDateTime now) {
        long days = ChronoUnit.DAYS.between(lastUpdated, now);
        return currentScore * (float) Math.exp(-LAMBDA * days);
    }

    /**
     * Returns the score delta associated with a given interaction type.
     *
     * @param action the interaction type
     * @return {@code WEIGHT_VIEW} (+0.1) for VIEW, {@code WEIGHT_JOIN} (+1.0) for JOIN,
     *         or {@code WEIGHT_SKIP} (-10.0) for SKIP
     */
    private float eventWeight(InteractionType action) {
        return switch (action) {
            case VIEW -> WEIGHT_VIEW;
            case JOIN -> WEIGHT_JOIN;
            case SKIP -> WEIGHT_SKIP;
        };
    }
}
