package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.domain.ports.in.JoinPatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Personalized patch feed")
public class JoinPatchController {

    private final JoinPatchUseCase joinPatchUseCase;

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
    @PostMapping("/{patchId}/join")
    public ResponseEntity<Void> joinPatch(
            @PathVariable UUID patchId,
            @RequestParam UUID userId) {

        joinPatchUseCase.execute(patchId, userId);
        return ResponseEntity.ok().build();
    }
}