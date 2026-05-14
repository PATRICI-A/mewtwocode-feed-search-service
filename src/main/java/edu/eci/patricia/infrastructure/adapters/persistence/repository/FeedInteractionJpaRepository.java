package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link FeedInteractionEntity}, providing CRUD operations
 * and custom queries for the {@code feed_interactions} table.
 */
public interface FeedInteractionJpaRepository extends JpaRepository<FeedInteractionEntity, UUID> {

    /**
     * Returns all interaction records for the given user, regardless of interaction type.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link FeedInteractionEntity} instances; never {@code null}, may be empty
     */
    List<FeedInteractionEntity> findByUserId(UUID userId);

    /**
     * Returns the distinct patch IDs for which the user has recorded a JOIN interaction.
     *
     * @param userId the unique identifier of the user
     * @return a list of patch UUIDs the user has joined; never {@code null}, may be empty
     */
    @Query("SELECT DISTINCT f.patchId FROM FeedInteractionEntity f WHERE f.userId = :userId AND f.action = 'JOIN'")
    List<UUID> findJoinedPatchIdsByUserId(@Param("userId") UUID userId);
}
