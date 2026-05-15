package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.enums.InteractionType;

import java.util.UUID;

/**
 * Inbound port (use-case boundary) for recording a user's interaction with a patch
 * in the feed. Persists the interaction event and triggers any side-effects such as
 * updating the user's category affinity scores.
 */
public interface RegisterInteractionPort {

    /**
     * Registers the interaction performed by a user on a specific patch.
     *
     * @param userId  the unique identifier of the user performing the interaction
     * @param patchId the unique identifier of the patch that was interacted with
     * @param action  the type of interaction that occurred (view, join, or skip)
     */
    void register(UUID userId, UUID patchId, InteractionType action);
}
