package edu.eci.patricia.domain.exceptions;

/**
 * Exception thrown when a domain business rule is violated.
 * This is an unchecked exception used to signal illegal states
 * or invalid operations that contradict the domain model's invariants.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Constructs a new {@code BusinessRuleException} with the specified detail message.
     *
     * @param message a human-readable description of the violated business rule
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
