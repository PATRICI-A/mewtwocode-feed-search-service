package edu.eci.patricia.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Domain model representing a single interest tag declared by a user.
 * Interest tags are used by the recommendation engine to personalise the
 * feed and boost patches whose description or category matches the user's
 * stated interests.
 */
public class UserInterest {
    /** Unique identifier for this interest record. */
    private UUID id;

    /** Identifier of the user who declared this interest. */
    private UUID userId;

    /** The interest tag string (e.g. a keyword, topic, or category label). */
    private String interestingTag;

    /** Timestamp at which this interest was registered. */
    private LocalDateTime createdAt;
}
