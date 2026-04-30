package edu.eci.patricia.application.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchRecommendationResponse {
    private UUID patchId;
    private PatchSummaryResponse patch;
    private Float affinityScore;
    private String reason;
}
