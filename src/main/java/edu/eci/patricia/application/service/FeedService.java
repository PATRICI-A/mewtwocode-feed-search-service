package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService implements FeedUseCase {

    private static final float INTEREST_SCORE     = 0.50f;
    private static final float TEMPORAL_SCORE_MAX = 0.20f;
    private static final float MAX_CAT_SCORE      = 100f;

    private final PatchRepositoryPort patchRepository;
    private final UserCategoryScoreRepositoryPort categoryScoreRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchDomainMapper mapper;

    public FeedService(PatchRepositoryPort patchRepository,
                       UserCategoryScoreRepositoryPort categoryScoreRepository,
                       MembershipRepositoryPort membershipRepository,
                       PatchDomainMapper mapper) {
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PatchSummaryResponse> execute(UUID userId, int page, int size) {
        Map<PatchCategory, Float> categoryScores = categoryScoreRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(UserCategoryScore::getCategory, UserCategoryScore::getScoreTotal));

        return patchRepository.findOpenPublicPatches().stream()
                .map(p -> {
                    float score = scoreRelevance(p, categoryScores);
                    boolean isMember = membershipRepository.existsActiveMembership(p.getId(), userId);
                    return mapper.toResponse(p, isMember, score);
                })
                .sorted(Comparator.comparing(
                        PatchSummaryResponse::getAffinityScore,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    private float scoreRelevance(Patch patch, Map<PatchCategory, Float> categoryScores) {
        float score = 0f;

        float catScore = categoryScores.getOrDefault(patch.getCategory(), 0f);
        if (catScore > 0) {
            score += Math.min(catScore / MAX_CAT_SCORE, 1.0f) * INTEREST_SCORE;
        }

        score += temporalScore(patch.getStartTime());

        return score;
    }

    private float temporalScore(LocalDateTime startTime) {
        if (startTime == null) return 0f;
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), startTime);
        if (hours < 0)    return 0f;
        if (hours <= 24)  return TEMPORAL_SCORE_MAX;
        if (hours <= 72)  return TEMPORAL_SCORE_MAX * 0.75f;
        if (hours <= 168) return TEMPORAL_SCORE_MAX * 0.50f;
        if (hours <= 720) return TEMPORAL_SCORE_MAX * 0.25f;
        return 0f;
    }
}
