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

    public RegisterInteractionService(FeedInteractionRepositoryPort interactionRepository,
                                      PatchRepositoryPort patchRepository,
                                      UserCategoryScoreRepositoryPort categoryScoreRepository) {
        this.interactionRepository = interactionRepository;
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
    }

    @Override
    public void register(UUID userId, UUID patchId, InteractionType action) {
        interactionRepository.save(userId, patchId, action);
        updateCategoryScore(userId, patchId, action);
    }

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

    private float applyDecay(float currentScore, LocalDateTime lastUpdated, LocalDateTime now) {
        long days = ChronoUnit.DAYS.between(lastUpdated, now);
        return currentScore * (float) Math.exp(-LAMBDA * days);
    }

    private float eventWeight(InteractionType action) {
        return switch (action) {
            case VIEW -> WEIGHT_VIEW;
            case JOIN -> WEIGHT_JOIN;
            case SKIP -> WEIGHT_SKIP;
        };
    }
}
