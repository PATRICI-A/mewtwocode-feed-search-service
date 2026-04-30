package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.UserInterest;
import java.util.List;
import java.util.UUID;

public interface UserInterestRepositoryPort {
    List<UserInterest> findByUserId(UUID userId);
}
