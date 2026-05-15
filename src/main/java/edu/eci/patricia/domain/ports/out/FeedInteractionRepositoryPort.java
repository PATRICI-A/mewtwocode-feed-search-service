package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.enums.InteractionType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Outbound port (secondary/driven port) that defines the persistence contract
 * for {@link edu.eci.patricia.domain.model.FeedInteraction} records. Implementations
 * are provided by the infrastructure layer and used by the domain to query and persist
 * user–patch interaction events.
 */
public interface FeedInteractionRepositoryPort {

    /**
     * Retrieves all feed interactions recorded for the specified user.
     *
     * @param userId the unique identifier of the user whose interactions are queried
     * @return a list of {@link FeedInteraction} records; never {@code null}, may be empty
     */
    List<FeedInteraction> findByUserId(UUID userId);

    /**
     * Returns the set of patch identifiers that the specified user has joined.
     * Used to exclude already-joined patches from feed and recommendation results.
     *
     * @param userId the unique identifier of the user
     * @return a set of patch UUIDs the user has joined; never {@code null}, may be empty
     */
    Set<UUID> findJoinedPatchIds(UUID userId);

    /**
     * Persists a new interaction record for the given user–patch pair.
     *
     * @param userId  the unique identifier of the user who performed the interaction
     * @param patchId the unique identifier of the patch that was interacted with
     * @param type    the type of interaction that occurred
     */
    void save(UUID userId, UUID patchId, InteractionType type);
}
