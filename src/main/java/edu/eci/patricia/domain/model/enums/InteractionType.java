package edu.eci.patricia.domain.model.enums;

/**
 * Describes the type of interaction a user performed with a patch in the feed.
 * Interaction records are used by the recommendation engine to compute and
 * update per-category affinity scores.
 *
 * <ul>
 *   <li>{@link #VIEW} - The user opened or previewed the patch details.</li>
 *   <li>{@link #JOIN} - The user joined the patch as a member.</li>
 *   <li>{@link #SKIP} - The user explicitly dismissed or skipped the patch.</li>
 * </ul>
 */
public enum InteractionType {
    VIEW, JOIN, SKIP
}
