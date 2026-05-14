package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Domain model that stores the aggregated affinity score of a user for a specific
 * patch category. Scores are updated each time the user interacts with a patch
 * (view, join, or skip) and are consumed by the recommendation engine to rank
 * and personalise the feed.
 */
public class UserCategoryScore {
    /** Unique identifier for this score record. */
    private UUID id;

    /** Identifier of the user this score belongs to. */
    private UUID userId;

    /** The patch category associated with this score. */
    private PatchCategory category;

    /** Accumulated affinity score for the category, updated after each interaction. */
    private float scoreTotal;

    /** Timestamp of the last time this score was recalculated. */
    private LocalDateTime lastUpdated;
}
