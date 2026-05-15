package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a user attempts to join a patch that has already
 * reached its maximum capacity. Enforces the capacity constraint defined
 * in the {@link edu.eci.patricia.domain.model.Patch} domain model.
 */
public class PatchFullException extends RuntimeException {

    /**
     * Constructs a new {@code PatchFullException} for the given patch identifier.
     *
     * @param patchId the unique identifier of the patch that is at full capacity
     */
    public PatchFullException(UUID patchId) {
        super("Patch " + patchId + " is full.");
    }
}