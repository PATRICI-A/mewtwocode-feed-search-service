package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

public class PatchNotFoundException extends RuntimeException {
    public PatchNotFoundException(UUID id) {
        super("Patch not found: " + id);
    }
}
