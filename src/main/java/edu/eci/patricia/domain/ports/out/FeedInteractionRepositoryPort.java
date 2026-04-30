package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.FeedInteraction;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FeedInteractionRepositoryPort {
    List<FeedInteraction> findByUserId(UUID userId);
    Set<UUID> findInteractedPatchIds(UUID userId);
}
