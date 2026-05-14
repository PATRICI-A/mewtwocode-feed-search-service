package edu.eci.patricia.application.dto.response;

import lombok.*;

import java.util.UUID;

/**
 * Data Transfer Object returned by the recommendation endpoint.
 * Wraps a {@link PatchSummaryResponse} together with the computed affinity score and a
 * human-readable explanation of why the patch was recommended to the user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchRecommendationResponse {
    /** Unique identifier of the recommended patch. */
    private UUID patchId;
    /** Full summary of the recommended patch. */
    private PatchSummaryResponse patch;
    /** Affinity score in the range [0.0, 1.0] representing relevance to the user's interests. */
    private Float affinityScore;
    /** Human-readable explanation of why this patch was recommended. */
    private String reason;
}
