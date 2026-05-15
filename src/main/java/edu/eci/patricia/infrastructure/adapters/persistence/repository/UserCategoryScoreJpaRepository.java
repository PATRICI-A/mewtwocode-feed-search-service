package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserCategoryScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserCategoryScoreEntity}, providing CRUD operations
 * and custom finders for the {@code user_category_scores} table.
 */
public interface UserCategoryScoreJpaRepository extends JpaRepository<UserCategoryScoreEntity, UUID> {

    /**
     * Returns all category affinity score records for the given user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link UserCategoryScoreEntity} instances; never {@code null}, may be empty
     */
    List<UserCategoryScoreEntity> findByUserId(UUID userId);

    /**
     * Returns the affinity score record for a specific user and category combination.
     *
     * @param userId   the unique identifier of the user
     * @param category the patch category to look up
     * @return an {@link Optional} containing the entity if a record exists, or empty otherwise
     */
    Optional<UserCategoryScoreEntity> findByUserIdAndCategory(UUID userId, PatchCategory category);
}
