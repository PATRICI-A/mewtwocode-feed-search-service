package edu.eci.patricia.domain.exceptions;

/**
 * Exception thrown when a downstream or external service is unavailable
 * and the operation cannot be completed. Wraps the original cause to
 * preserve the full error chain for diagnostics.
 */
public class ServiceUnavailableException extends RuntimeException {

    /**
     * Constructs a new {@code ServiceUnavailableException} with the specified
     * detail message and the underlying cause.
     *
     * @param message a human-readable description of the unavailability
     * @param cause   the original exception that triggered this failure
     */
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
