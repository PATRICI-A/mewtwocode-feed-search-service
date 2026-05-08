package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FeedInteractionJpaRepository extends JpaRepository<FeedInteractionEntity, UUID> {
    List<FeedInteractionEntity> findByUserId(UUID userId);

    @Query("SELECT DISTINCT f.patchId FROM FeedInteractionEntity f WHERE f.userId = :userId AND f.action = 'JOIN'")
    List<UUID> findJoinedPatchIdsByUserId(@Param("userId") UUID userId);
}
