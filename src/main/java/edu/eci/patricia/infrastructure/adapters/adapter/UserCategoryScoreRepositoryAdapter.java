package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserCategoryScoreEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserCategoryScoreJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter that bridges the {@link UserCategoryScoreRepositoryPort} domain port
 * and the JPA-backed {@link UserCategoryScoreJpaRepository}.
 *
 * <p>Also contains inline mapping helpers to convert between the
 * {@link UserCategoryScoreEntity} persistence model and the {@link UserCategoryScore} domain model.</p>
 */
@Component
public class UserCategoryScoreRepositoryAdapter implements UserCategoryScoreRepositoryPort {

    private final UserCategoryScoreJpaRepository jpaRepository;

    /**
     * Constructs the adapter with its required repository dependency.
     *
     * @param jpaRepository the JPA repository for {@code user_category_scores} table
     */
    public UserCategoryScoreRepositoryAdapter(UserCategoryScoreJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Retrieves all category affinity scores for the given user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link UserCategoryScore} domain objects; never {@code null}, may be empty
     */
    @Override
    public List<UserCategoryScore> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Retrieves the affinity score of a specific user for a specific category.
     *
     * @param userId   the unique identifier of the user
     * @param category the patch category to look up
     * @return an {@link Optional} containing the score if found, or empty if no record exists
     */
    @Override
    public Optional<UserCategoryScore> findByUserIdAndCategory(UUID userId, PatchCategory category) {
        return jpaRepository.findByUserIdAndCategory(userId, category).map(this::toDomain);
    }

    /**
     * Persists a category affinity score, inserting or updating the existing record.
     *
     * @param score the domain object containing the score data to persist
     * @return the persisted {@link UserCategoryScore} domain object after saving
     */
    @Override
    public UserCategoryScore save(UserCategoryScore score) {
        UserCategoryScoreEntity entity = toEntity(score);
        return toDomain(jpaRepository.save(entity));
    }

    /**
     * Converts a {@link UserCategoryScoreEntity} persistence entity to a {@link UserCategoryScore} domain object.
     *
     * @param e the entity to convert
     * @return the corresponding domain object
     */
    private UserCategoryScore toDomain(UserCategoryScoreEntity e) {
        return UserCategoryScore.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .category(e.getCategory())
                .scoreTotal(e.getScoreTotal())
                .lastUpdated(e.getLastUpdated())
                .build();
    }

    /**
     * Converts a {@link UserCategoryScore} domain object to a {@link UserCategoryScoreEntity} persistence entity.
     *
     * @param s the domain object to convert
     * @return the corresponding persistence entity
     */
    private UserCategoryScoreEntity toEntity(UserCategoryScore s) {
        return UserCategoryScoreEntity.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .category(s.getCategory())
                .scoreTotal(s.getScoreTotal())
                .lastUpdated(s.getLastUpdated())
                .build();
    }
}
