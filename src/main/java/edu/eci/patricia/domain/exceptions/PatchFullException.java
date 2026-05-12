package edu.eci.patricia.domain.exceptions;

import java.util.UUID;

public class PatchFullException extends RuntimeException {
    public PatchFullException(UUID patchId) {
        super("Patch " + patchId + " is full.");
    }
}