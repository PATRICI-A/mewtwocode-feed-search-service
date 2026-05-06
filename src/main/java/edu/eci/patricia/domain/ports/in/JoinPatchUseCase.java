package edu.eci.patricia.domain.ports.in;

import java.util.UUID;

public interface JoinPatchUseCase {
    void execute(UUID patchId, UUID userId);
}