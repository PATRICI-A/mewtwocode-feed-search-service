package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link UserInterestEntity}, providing CRUD operations
 * and a custom finder for the {@code user_interests} table.
 */
public interface UserInterestJpaRepository extends JpaRepository<UserInterestEntity, UUID> {

    /**
     * Returns all interest tag records associated with the given user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link UserInterestEntity} instances; never {@code null}, may be empty
     */
    List<UserInterestEntity> findByUserId(UUID userId);
}
