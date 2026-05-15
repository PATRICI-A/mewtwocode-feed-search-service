package edu.eci.patricia.domain.model.enums;

/**
 * Represents the current availability state of a patch throughout its lifecycle.
 *
 * <ul>
 *   <li>{@link #OPEN}      - The patch is accepting new members.</li>
 *   <li>{@link #FULL}      - The patch has reached its maximum capacity.</li>
 *   <li>{@link #CLOSED}    - The patch is no longer accepting members (e.g., activity ended).</li>
 *   <li>{@link #CANCELLED} - The patch was cancelled by its creator before taking place.</li>
 * </ul>
 */
public enum PatchStatus {
    OPEN, FULL, CLOSED, CANCELLED
}
