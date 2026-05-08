package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.PatchCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCategoryScoreRepositoryPort {
    List<UserCategoryScore> findByUserId(UUID userId);
    Optional<UserCategoryScore> findByUserIdAndCategory(UUID userId, PatchCategory category);
    UserCategoryScore save(UserCategoryScore score);
}
