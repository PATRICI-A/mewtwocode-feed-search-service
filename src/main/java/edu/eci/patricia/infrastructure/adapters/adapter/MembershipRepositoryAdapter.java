package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchMembershipJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class MembershipRepositoryAdapter implements MembershipRepositoryPort {

    private final PatchMembershipJpaRepository jpaRepository;

    public MembershipRepositoryAdapter(PatchMembershipJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsActiveMembership(UUID patchId, UUID userId) {
        return jpaRepository.existsByPatchIdAndUserIdAndStatus(
                patchId, userId, MembershipStatus.ACTIVE);
    }

    @Override
    public Set<UUID> findActivePatchIds(UUID userId, List<UUID> patchIds) {
        if (patchIds.isEmpty()) return Set.of();
        return jpaRepository.findActivePatchIdsByUserIdAndPatchIds(userId, patchIds);
    }
}