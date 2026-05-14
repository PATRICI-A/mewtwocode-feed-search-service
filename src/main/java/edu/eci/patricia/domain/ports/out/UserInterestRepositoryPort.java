package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.UserInterest;

import java.util.List;
import java.util.UUID;

/**
 * Outbound port (secondary/driven port) that defines the persistence contract
 * for {@link edu.eci.patricia.domain.model.UserInterest} entities. Implementations
 * are provided by the infrastructure layer (e.g., a JPA adapter).
 */
public interface UserInterestRepositoryPort {

    /**
     * Retrieves all interest tags registered for the specified user.
     *
     * @param userId the unique identifier of the user whose interests are queried
     * @return a list of {@link UserInterest} records; never {@code null}, may be empty
     */
    List<UserInterest> findByUserId(UUID userId);
}
