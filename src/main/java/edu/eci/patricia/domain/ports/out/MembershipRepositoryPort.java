package edu.eci.patricia.domain.ports.out;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Outbound port (secondary/driven port) that defines the read-side persistence
 * contract for patch membership data. Allows the domain to query active membership
 * relationships without depending on the infrastructure layer directly.
 */
public interface MembershipRepositoryPort {

    /**
     * Checks whether the specified user holds an active membership in the given patch.
     *
     * @param patchId the unique identifier of the patch to check
     * @param userId  the unique identifier of the user to check
     * @return {@code true} if an active membership exists; {@code false} otherwise
     */
    boolean existsActiveMembership(UUID patchId, UUID userId);

    /**
     * Filters the provided list of patch identifiers and returns only those in which
     * the specified user currently holds an active membership.
     *
     * @param userId   the unique identifier of the user
     * @param patchIds the candidate list of patch identifiers to check
     * @return a set containing the patch UUIDs from {@code patchIds} where the user is active;
     *         never {@code null}, may be empty
     */
    Set<UUID> findActivePatchIds(UUID userId, List<UUID> patchIds);
}