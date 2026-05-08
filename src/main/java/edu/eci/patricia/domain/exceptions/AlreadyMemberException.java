package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException(UUID patchId, UUID userId) {
        super("Usuario " + userId + " ya es miembro del parche " + patchId);
    }
}
