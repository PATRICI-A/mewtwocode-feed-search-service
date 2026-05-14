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

/**
 * REST controller that exposes the patch recommendation endpoint under {@code /api/v1/feed}.
 *
 * <p>Returns a personalised ranked list of recommended patches for a user based on category
 * affinity scores. New users with no interaction history fall back to the most popular patches
 * on campus.</p>
 */
@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Personalized patch recommendations")
public class RecommendationController {

    private final GetRecommendationsPort getRecommendationsPort;
    private final PatchDomainMapper mapper;

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param getRecommendationsPort the port that computes and returns patch recommendations
     * @param mapper                 the mapper used to convert domain objects to API response DTOs
     */
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
    /**
     * Returns up to 10 recommended patches for the given user, ranked by affinity score descending.
     *
     * @param userId the UUID of the authenticated user requesting recommendations
     * @return a 200 response containing a list of {@link PatchRecommendationResponse} objects;
     *         may be empty but never {@code null}
     */
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
