package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException(UUID patchId, UUID userId) {
        super("User " + userId + " is already a member of patch " + patchId);
    }
}
