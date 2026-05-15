package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchMembershipJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Infrastructure adapter that bridges the {@link MembershipRepositoryPort} domain port
 * and the JPA-backed {@link PatchMembershipJpaRepository}.
 *
 * <p>Provides read-only membership queries used by the feed and search use cases
 * to determine whether a user is already a member of one or more patches.</p>
 */
@Component
public class MembershipRepositoryAdapter implements MembershipRepositoryPort {

    private final PatchMembershipJpaRepository jpaRepository;

    /**
     * Constructs the adapter with its required repository dependency.
     *
     * @param jpaRepository the JPA repository for {@code patch_memberships} table
     */
    public MembershipRepositoryAdapter(PatchMembershipJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Checks whether an active membership record exists for the given patch and user combination.
     *
     * @param patchId the unique identifier of the patch
     * @param userId  the unique identifier of the user
     * @return {@code true} if an active membership exists; {@code false} otherwise
     */
    @Override
    public boolean existsActiveMembership(UUID patchId, UUID userId) {
        return jpaRepository.existsByPatchIdAndUserIdAndStatus(
                patchId, userId, MembershipStatus.ACTIVE);
    }

    /**
     * Returns the subset of the supplied patch IDs for which the user holds an active membership.
     *
     * @param userId   the unique identifier of the user
     * @param patchIds the candidate list of patch IDs to check; if empty, an empty set is returned immediately
     * @return a {@link Set} of patch UUIDs from {@code patchIds} where the user is an active member
     */
    @Override
    public Set<UUID> findActivePatchIds(UUID userId, List<UUID> patchIds) {
        if (patchIds.isEmpty()) return Set.of();
        return jpaRepository.findActivePatchIdsByUserIdAndPatchIds(userId, patchIds);
    }
}