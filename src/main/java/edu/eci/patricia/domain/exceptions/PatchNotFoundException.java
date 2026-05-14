package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a patch cannot be located in the repository
 * using the provided identifier. Signals that the requested patch
 * does not exist or has been deleted.
 */
public class PatchNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code PatchNotFoundException} for the given patch identifier.
     *
     * @param id the unique identifier of the patch that was not found
     */
    public PatchNotFoundException(UUID id) {
        super("Patch not found: " + id);
    }
}
