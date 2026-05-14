package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.InteractionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Domain model representing a recorded interaction between a user and a patch
 * inside the feed. Each interaction captures which user acted on which patch,
 * when the action occurred, and what type of action was performed.
 * These records are used by the recommendation engine to adjust affinity scores.
 */
public class FeedInteraction {
    /** Unique identifier for this interaction record. */
    private UUID id;

    /** Identifier of the user who performed the interaction. */
    private UUID userId;

    /** Identifier of the patch that was the target of the interaction. */
    private UUID patchId;

    /** Timestamp at which the interaction was registered. */
    private LocalDateTime interactedAt;

    /** The type of action the user performed on the patch. */
    private InteractionType action;
}
