package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port (secondary/driven port) that defines the persistence contract
 * for {@link edu.eci.patricia.domain.model.UserCategoryScore} records. Used by the
 * recommendation engine to read and update a user's per-category affinity scores.
 */
public interface UserCategoryScoreRepositoryPort {

    /**
     * Retrieves all category affinity scores for the specified user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link UserCategoryScore} records; never {@code null}, may be empty
     */
    List<UserCategoryScore> findByUserId(UUID userId);

    /**
     * Retrieves the affinity score for a specific user–category combination.
     *
     * @param userId   the unique identifier of the user
     * @param category the patch category whose score is requested
     * @return an {@link Optional} containing the score if it exists, or empty if none has been recorded yet
     */
    Optional<UserCategoryScore> findByUserIdAndCategory(UUID userId, PatchCategory category);

    /**
     * Persists (inserts or updates) the given category affinity score.
     *
     * @param score the {@link UserCategoryScore} to save
     * @return the saved {@link UserCategoryScore}, potentially with updated persistence metadata
     */
    UserCategoryScore save(UserCategoryScore score);
}
