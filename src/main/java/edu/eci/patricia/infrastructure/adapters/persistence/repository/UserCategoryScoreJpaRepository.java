package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserCategoryScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCategoryScoreJpaRepository extends JpaRepository<UserCategoryScoreEntity, UUID> {
    List<UserCategoryScoreEntity> findByUserId(UUID userId);
    Optional<UserCategoryScoreEntity> findByUserIdAndCategory(UUID userId, PatchCategory category);
}
