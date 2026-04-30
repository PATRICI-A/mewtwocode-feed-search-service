package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "feed", description = "Feed personalizado de parches")
public class FeedController {

    private final FeedUseCase feedUseCase;

    public FeedController(FeedUseCase feedUseCase) {
        this.feedUseCase = feedUseCase;
    }

    @Operation(
            summary = "Obtener feed de parches",
            description = "Retorna lista paginada de parches activos y públicos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed retornado exitosamente"),
            @ApiResponse(responseCode = "401", description = "JWT inválido o ausente"),
            @ApiResponse(responseCode = "503", description = "Servicio dependiente no disponible")
    })
    @GetMapping("/parches")
    public ResponseEntity<List<PatchSummaryResponse>> getFeed(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        List<PatchSummaryResponse> response = feedUseCase.execute(userId, page, size);
        return ResponseEntity.ok(response);
    }
}
