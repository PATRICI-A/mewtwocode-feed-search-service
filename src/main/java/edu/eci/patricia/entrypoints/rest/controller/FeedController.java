package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.FeedFilterRequest;
import edu.eci.patricia.application.dto.request.InteractRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.in.RegisterInteractionPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller that exposes the personalised patch feed and interaction registration endpoints
 * under {@code /api/v1/feed}.
 *
 * <p>The feed endpoint returns public, open patches scored and ranked by category affinity,
 * temporal proximity, and optional filters. The interaction endpoint records a user action on a
 * patch and updates the underlying category affinity score.</p>
 */
@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Personalized patch feed")
public class FeedController {

    private final FeedUseCase feedUseCase;
    private final RegisterInteractionPort registerInteraction;

    /**
     * Constructs the controller with its required use-case dependencies.
     *
     * @param feedUseCase         the use case that builds and ranks the personalised feed
     * @param registerInteraction the port that records a user interaction with a patch
     */
    public FeedController(FeedUseCase feedUseCase, RegisterInteractionPort registerInteraction) {
        this.feedUseCase = feedUseCase;
        this.registerInteraction = registerInteraction;
    }

    @Operation(
            summary = "Get personalized patch feed",
            description = "Returns active and public patches ordered by relevance score. " +
                    "Score combines: category affinity (50%) + temporal proximity (20%) + " +
                    "geographic proximity (30%, pending integration). " +
                    "Optional filters narrow results before scoring and pagination are applied."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed returned successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT"),
            @ApiResponse(responseCode = "503", description = "Dependent service unavailable")
    })
    /**
     * Returns a personalised, paginated list of public open patches scored for the given user.
     *
     * @param userId     the UUID of the authenticated user requesting the feed
     * @param category   optional filter to restrict results to a specific {@link PatchCategory}
     * @param campusZone optional filter to restrict results to a specific {@link CampusZone}
     * @param dateFrom   optional inclusive lower bound on patch start date
     * @param page       zero-based page index (defaults to 0)
     * @param size       number of items per page, maximum 20 (defaults to 20)
     * @return a 200 response containing the ordered list of {@link PatchSummaryResponse} objects
     */
    @GetMapping("/parches")
    public ResponseEntity<List<PatchSummaryResponse>> getFeed(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestParam UUID userId,
            @Parameter(description = "Filter by patch category", example = "SPORTS")
            @RequestParam(required = false) PatchCategory category,
            @Parameter(description = "Filter by campus zone", example = "CAFETERIA")
            @RequestParam(required = false) CampusZone campusZone,
            @Parameter(description = "Filter patches starting from this date (inclusive)", example = "2025-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Results per page (max 20)", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        FeedFilterRequest filters = FeedFilterRequest.builder()
                .category(category)
                .campusZone(campusZone)
                .dateFrom(dateFrom)
                .build();

        List<PatchSummaryResponse> response = feedUseCase.execute(userId, filters, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Register interaction with a patch",
            description = "Records a user action on a patch and updates the category affinity score " +
                    "using time decay formula: S = S_current × e^(-0.01 × days) + event_weight. " +
                    "Event weights — VIEW: +0.1 | JOIN: +1.0 | SKIP: -10.0. " +
                    "Category score is clamped between 0 and 100 and drives feed and recommendations ranking. " +
                    "JOIN also excludes the patch from future recommendations."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Interaction recorded and category score updated"),
            @ApiResponse(responseCode = "400", description = "Invalid action or patch not found"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT")
    })
    /**
     * Records a user interaction (view, join, or skip) with a specific patch and updates
     * the user's category affinity score accordingly.
     *
     * @param patchId the UUID of the patch being interacted with
     * @param userId  the UUID of the authenticated user performing the interaction
     * @param request the request body containing the {@link edu.eci.patricia.domain.model.enums.InteractionType}
     * @return a 204 No Content response on success
     */
    @PostMapping("/parches/{patchId}/interact")
    public ResponseEntity<Void> interact(
            @Parameter(description = "ID of the patch to interact with", required = true)
            @PathVariable UUID patchId,
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestParam UUID userId,
            @Valid @RequestBody InteractRequest request) {

        registerInteraction.register(userId, patchId, request.getAction());
        return ResponseEntity.noContent().build();
    }
}
