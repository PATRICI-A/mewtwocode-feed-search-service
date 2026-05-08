package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PatchMembershipJpaRepository extends JpaRepository<PatchMembershipEntity, UUID> {

    boolean existsByPatchIdAndUserIdAndStatus(UUID patchId, UUID userId, MembershipStatus status);

    @Query("SELECT m.patchId FROM PatchMembershipEntity m WHERE m.userId = :userId AND m.patchId IN :patchIds AND m.status = 'ACTIVE'")
    Set<UUID> findActivePatchIdsByUserIdAndPatchIds(@Param("userId") UUID userId, @Param("patchIds") List<UUID> patchIds);
}