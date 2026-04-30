package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatchJpaRepository extends JpaRepository<PatchEntity, UUID> {
    List<PatchEntity> findByStatusAndIsPublic(PatchStatus status, boolean isPublic);
}
