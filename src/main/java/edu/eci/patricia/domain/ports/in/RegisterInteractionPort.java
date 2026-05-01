package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.enums.InteractionType;

import java.util.UUID;

public interface RegisterInteractionPort {
    void register(UUID userId, UUID patchId, InteractionType action);
}
