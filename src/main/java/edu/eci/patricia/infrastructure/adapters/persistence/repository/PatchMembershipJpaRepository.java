package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link PatchMembershipEntity}, providing CRUD operations
 * and custom membership-check queries for the {@code patch_memberships} table.
 */
public interface PatchMembershipJpaRepository extends JpaRepository<PatchMembershipEntity, UUID> {

    /**
     * Checks whether a membership record exists for the given patch, user, and status.
     *
     * @param patchId the unique identifier of the patch
     * @param userId  the unique identifier of the user
     * @param status  the membership status to match
     * @return {@code true} if a matching record exists; {@code false} otherwise
     */
    boolean existsByPatchIdAndUserIdAndStatus(UUID patchId, UUID userId, MembershipStatus status);

    /**
     * Returns the subset of the supplied patch IDs for which the user holds an active membership.
     *
     * @param userId   the unique identifier of the user
     * @param patchIds the candidate list of patch IDs to filter
     * @return a {@link Set} of patch UUIDs from {@code patchIds} where the user is an active member
     */
    @Query("SELECT m.patchId FROM PatchMembershipEntity m WHERE m.userId = :userId AND m.patchId IN :patchIds AND m.status = 'ACTIVE'")
    Set<UUID> findActivePatchIdsByUserIdAndPatchIds(@Param("userId") UUID userId, @Param("patchIds") List<UUID> patchIds);
}