package edu.eci.patricia.domain.model.enums;

/**
 * Represents the lifecycle status of a user's membership in a patch.
 *
 * <ul>
 *   <li>{@link #ACTIVE} - The user is a current, active member of the patch.</li>
 *   <li>{@link #LEFT}   - The user voluntarily left the patch.</li>
 *   <li>{@link #KICKED} - The user was removed from the patch by its creator.</li>
 * </ul>
 */
public enum MembershipStatus {
    ACTIVE, LEFT, KICKED
}
