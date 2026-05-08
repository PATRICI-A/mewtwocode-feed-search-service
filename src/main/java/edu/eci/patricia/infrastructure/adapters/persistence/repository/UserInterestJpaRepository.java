package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserInterestJpaRepository extends JpaRepository<UserInterestEntity, UUID> {
    List<UserInterestEntity> findByUserId(UUID userId);
}
