package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.request.FeedFilterRequest;
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
import java.util.stream.Stream;

/**
 * Application service that builds and ranks the personalised patch feed for a given user.
 *
 * <p>Each candidate patch receives a composite relevance score composed of two components:
 * <ul>
 *   <li><strong>Interest score</strong> — up to {@code INTEREST_SCORE} (0.50) based on how
 *       closely the patch category matches the user's accumulated category scores.</li>
 *   <li><strong>Temporal score</strong> — up to {@code TEMPORAL_SCORE_MAX} (0.20) that decays
 *       as the patch start time moves further into the future.</li>
 * </ul>
 *
 * <p>Only open, public patches are considered. Optional {@link FeedFilterRequest} predicates
 * (category, campus zone, date) are applied before scoring. Results are returned in descending
 * score order with offset-based pagination.
 */
@Service
public class FeedService implements FeedUseCase {

    private static final float INTEREST_SCORE     = 0.50f;
    private static final float TEMPORAL_SCORE_MAX = 0.20f;
    private static final float MAX_CAT_SCORE      = 100f;

    private final PatchRepositoryPort patchRepository;
    private final UserCategoryScoreRepositoryPort categoryScoreRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchDomainMapper mapper;

    /**
     * Constructs a {@code FeedService} with all required collaborators.
     *
     * @param patchRepository         port for reading patch data
     * @param categoryScoreRepository port for reading per-user category scores
     * @param membershipRepository    port for checking active memberships
     * @param mapper                  mapper that converts domain patches to response DTOs
     */
    public FeedService(PatchRepositoryPort patchRepository,
                       UserCategoryScoreRepositoryPort categoryScoreRepository,
                       MembershipRepositoryPort membershipRepository,
                       PatchDomainMapper mapper) {
        this.patchRepository = patchRepository;
        this.categoryScoreRepository = categoryScoreRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
    }

    /**
     * Executes the feed use case: retrieves, filters, scores, and paginates open public patches
     * personalised to the specified user.
     *
     * @param userId  the identifier of the requesting user; used to load category scores and
     *                determine membership
     * @param filters optional filter criteria (category, campus zone, date from); may be {@code null}
     * @param page    zero-based page index
     * @param size    maximum number of results to return
     * @return an ordered list of {@link PatchSummaryResponse} objects for the requested page,
     *         sorted by descending affinity score
     */
    @Override
    public List<PatchSummaryResponse> execute(UUID userId, FeedFilterRequest filters, int page, int size) {
        Map<PatchCategory, Float> categoryScores = categoryScoreRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(UserCategoryScore::getCategory, UserCategoryScore::getScoreTotal));

        Stream<Patch> stream = patchRepository.findOpenPublicPatches().stream();

        if (filters != null) {
            if (filters.getCategory() != null) {
                stream = stream.filter(p -> p.getCategory() == filters.getCategory());
            }
            if (filters.getCampusZone() != null) {
                stream = stream.filter(p -> p.getCampusZone() == filters.getCampusZone());
            }
            if (filters.getDateFrom() != null) {
                LocalDateTime from = filters.getDateFrom().atStartOfDay();
                stream = stream.filter(p -> p.getStartTime() != null && !p.getStartTime().isBefore(from));
            }
        }

        return stream
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

    /**
     * Computes the composite relevance score for a single patch by combining the interest
     * and temporal components.
     *
     * @param patch          the patch to score
     * @param categoryScores map of category to accumulated user score (0–100)
     * @return a non-negative composite score; higher values indicate greater relevance
     */
    private float scoreRelevance(Patch patch, Map<PatchCategory, Float> categoryScores) {
        float score = 0f;

        float catScore = categoryScores.getOrDefault(patch.getCategory(), 0f);
        if (catScore > 0) {
            score += Math.min(catScore / MAX_CAT_SCORE, 1.0f) * INTEREST_SCORE;
        }

        score += temporalScore(patch.getStartTime());

        return score;
    }

    /**
     * Calculates the temporal component of the relevance score based on how far in the future
     * the patch starts. Patches starting within 24 hours receive the full {@code TEMPORAL_SCORE_MAX};
     * the score degrades in steps up to 30 days, after which it falls to zero.
     *
     * @param startTime the scheduled start time of the patch; returns {@code 0} if {@code null}
     *                  or already in the past
     * @return a value in the range [0.0, {@code TEMPORAL_SCORE_MAX}]
     */
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
