package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link PatchEntity}, combining standard CRUD operations
 * with {@link JpaSpecificationExecutor} to support dynamic criteria-based queries
 * against the {@code patches} table.
 */
public interface PatchJpaRepository extends JpaRepository<PatchEntity, UUID>, JpaSpecificationExecutor<PatchEntity> {

    /**
     * Returns all patches that match the given status and visibility flag.
     *
     * @param status   the lifecycle status to filter by (e.g. OPEN)
     * @param isPublic {@code true} to include only public patches
     * @return a list of matching {@link PatchEntity} instances; never {@code null}, may be empty
     */
    List<PatchEntity> findByStatusAndIsPublic(PatchStatus status, boolean isPublic);

    /**
     * Returns the top 10 patches matching the given status and visibility, ordered by
     * {@code currentCount} descending (most participants first).
     *
     * @param status   the lifecycle status to filter by (e.g. OPEN)
     * @param isPublic {@code true} to include only public patches
     * @return a list of up to 10 {@link PatchEntity} instances sorted by participant count
     */
    List<PatchEntity> findTop10ByStatusAndIsPublicOrderByCurrentCountDesc(PatchStatus status, boolean isPublic);
}
