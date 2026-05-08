package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserCategoryScoreEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserCategoryScoreJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserCategoryScoreRepositoryAdapter implements UserCategoryScoreRepositoryPort {

    private final UserCategoryScoreJpaRepository jpaRepository;

    public UserCategoryScoreRepositoryAdapter(UserCategoryScoreJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<UserCategoryScore> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<UserCategoryScore> findByUserIdAndCategory(UUID userId, PatchCategory category) {
        return jpaRepository.findByUserIdAndCategory(userId, category).map(this::toDomain);
    }

    @Override
    public UserCategoryScore save(UserCategoryScore score) {
        UserCategoryScoreEntity entity = toEntity(score);
        return toDomain(jpaRepository.save(entity));
    }

    private UserCategoryScore toDomain(UserCategoryScoreEntity e) {
        return UserCategoryScore.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .category(e.getCategory())
                .scoreTotal(e.getScoreTotal())
                .lastUpdated(e.getLastUpdated())
                .build();
    }

    private UserCategoryScoreEntity toEntity(UserCategoryScore s) {
        return UserCategoryScoreEntity.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .category(s.getCategory())
                .scoreTotal(s.getScoreTotal())
                .lastUpdated(s.getLastUpdated())
                .build();
    }
}
