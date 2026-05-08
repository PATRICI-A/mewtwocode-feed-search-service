package edu.eci.patricia.domain.ports.out;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MembershipRepositoryPort {
    boolean existsActiveMembership(UUID patchId, UUID userId);
    Set<UUID> findActivePatchIds(UUID userId, List<UUID> patchIds);
}