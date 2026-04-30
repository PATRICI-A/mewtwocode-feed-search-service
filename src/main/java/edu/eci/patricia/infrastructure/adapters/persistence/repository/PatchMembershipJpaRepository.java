package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PatchMembershipJpaRepository extends JpaRepository<PatchMembershipEntity, UUID> {

    boolean existsByPatchIdAndUserIdAndStatus(UUID patchId, UUID userId, MembershipStatus status);
}