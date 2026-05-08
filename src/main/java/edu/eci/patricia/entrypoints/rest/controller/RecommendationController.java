package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.response.PatchRecommendationResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.ports.in.GetRecommendationsPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Personalized patch recommendations")
public class RecommendationController {

    private final GetRecommendationsPort getRecommendationsPort;
    private final PatchDomainMapper mapper;

    public RecommendationController(GetRecommendationsPort getRecommendationsPort, PatchDomainMapper mapper) {
        this.getRecommendationsPort = getRecommendationsPort;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Get patch recommendations",
            description = "Returns up to 10 recommended patches ranked by category affinity score. " +
                    "New users with no interaction history receive the most popular patches on campus."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendation list returned (may be empty)"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT"),
            @ApiResponse(responseCode = "503", description = "Dependent service unavailable")
    })
    @GetMapping("/recommended")
    public ResponseEntity<List<PatchRecommendationResponse>> getRecommendations(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestParam UUID userId) {

        List<PatchRecommendationResponse> response = getRecommendationsPort.getRecommendations(userId)
                .stream()
                .map(sp -> PatchRecommendationResponse.builder()
                        .patchId(sp.getPatchId())
                        .patch(mapper.toResponse(sp.getPatch(), false, sp.getAffinityScore()))
                        .affinityScore(sp.getAffinityScore())
                        .reason(sp.getReason())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}
