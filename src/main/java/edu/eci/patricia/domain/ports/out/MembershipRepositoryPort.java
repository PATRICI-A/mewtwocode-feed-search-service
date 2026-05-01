package edu.eci.patricia.domain.ports.out;

import java.util.UUID;

public interface MembershipRepositoryPort {
    boolean existsActiveMembership(UUID patchId, UUID userId);
}