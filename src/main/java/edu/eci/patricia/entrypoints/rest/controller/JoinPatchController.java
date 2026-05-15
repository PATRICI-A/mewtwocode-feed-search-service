package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.domain.ports.in.JoinPatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller that handles the join-patch operation under {@code /api/v1/feed}.
 *
 * <p>Allows an authenticated user to join a public, open patch that still has available spots.
 * Business rules such as capacity enforcement, duplicate membership detection, and patch status
 * validation are delegated to the {@link JoinPatchUseCase}.</p>
 */
@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Personalized patch feed")
public class JoinPatchController {

    private final JoinPatchUseCase joinPatchUseCase;

    /**
     * Constructs the controller with its required use-case dependency.
     *
     * @param joinPatchUseCase the use case that executes the join logic and enforces business rules
     */
    public JoinPatchController(JoinPatchUseCase joinPatchUseCase) {
        this.joinPatchUseCase = joinPatchUseCase;
    }

    @Operation(
            summary = "Join a patch from the feed",
            description = "Adds the authenticated user to a public patch that has available spots."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User joined the patch successfully"),
            @ApiResponse(responseCode = "404", description = "Patch not found"),
            @ApiResponse(responseCode = "409", description = "User is already a member of this patch"),
            @ApiResponse(responseCode = "422", description = "Patch is full or not open"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT")
    })
    /**
     * Adds the given user to the specified patch as an active member.
     *
     * @param patchId the UUID of the patch to join
     * @param userId  the UUID of the authenticated user requesting to join
     * @return a 200 OK response with no body on success
     */
    @PostMapping("/{patchId}/join")
    public ResponseEntity<Void> joinPatch(
            @PathVariable UUID patchId,
            @RequestParam UUID userId) {

        joinPatchUseCase.execute(patchId, userId);
        return ResponseEntity.ok().build();
    }
}