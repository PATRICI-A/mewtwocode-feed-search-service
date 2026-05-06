package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.enums.InteractionType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeedInteractionRepositoryPort {
    List<FeedInteraction> findByUserId(UUID userId);
    Set<UUID> findJoinedPatchIds(UUID userId);
    void save(UUID userId, UUID patchId, InteractionType type);
}
