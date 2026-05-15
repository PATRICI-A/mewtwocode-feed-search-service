package edu.eci.patricia.domain.valueobjects;

import edu.eci.patricia.domain.model.Patch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
/**
 * Immutable value object that pairs a {@link Patch} with its computed affinity
 * score and a human-readable explanation of why it was recommended. Used by the
 * recommendation engine to rank and present personalised patch suggestions to a user.
 */
public class ScoredPatch {
    /** The patch that has been scored and is being recommended. */
    private final Patch patch;

    /**
     * Numerical affinity score indicating how relevant this patch is for the user.
     * Higher values represent stronger relevance.
     */
    private final Float affinityScore;

    /** Human-readable explanation of the scoring rationale shown to the user. */
    private final String reason;

    /**
     * Returns the unique identifier of the underlying patch.
     * Convenience method that delegates to {@link Patch#getId()}.
     *
     * @return the UUID of the scored patch
     */
    public UUID getPatchId() {
        return patch.getId();
    }
}
