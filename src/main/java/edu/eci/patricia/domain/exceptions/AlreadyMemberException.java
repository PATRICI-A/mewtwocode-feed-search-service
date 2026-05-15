package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a user attempts to join a patch they are already
 * an active member of. Prevents duplicate membership records in the domain.
 */
public class AlreadyMemberException extends RuntimeException {

    /**
     * Constructs a new {@code AlreadyMemberException} for the given patch and user.
     *
     * @param patchId the unique identifier of the patch the user tried to join
     * @param userId  the unique identifier of the user who is already a member
     */
    public AlreadyMemberException(UUID patchId, UUID userId) {
        super("User " + userId + " is already a member of patch " + patchId);
    }
}
