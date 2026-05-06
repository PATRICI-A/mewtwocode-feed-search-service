package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.InteractRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.in.RegisterInteractionPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "feed", description = "Feed personalizado de parches")
public class FeedController {

    private final FeedUseCase feedUseCase;
    private final RegisterInteractionPort registerInteraction;

    public FeedController(FeedUseCase feedUseCase, RegisterInteractionPort registerInteraction) {
        this.feedUseCase = feedUseCase;
        this.registerInteraction = registerInteraction;
    }

    @Operation(
            summary = "Obtener feed personalizado de parches",
            description = "Retorna parches activos y públicos ordenados por relevancia. " +
                    "El score combina: intereses del usuario (50%) + cercanía temporal (20%) + " +
                    "proximidad geográfica (30%, pendiente integración). " +
                    "Los parches con mayor score aparecen primero."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed retornado exitosamente"),
            @ApiResponse(responseCode = "401", description = "JWT inválido o ausente"),
            @ApiResponse(responseCode = "503", description = "Servicio dependiente no disponible")
    })
    @GetMapping("/parches")
    public ResponseEntity<List<PatchSummaryResponse>> getFeed(
            @Parameter(description = "ID del usuario autenticado", required = true)
            @RequestParam UUID userId,
            @Parameter(description = "Número de página (base 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Resultados por página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        List<PatchSummaryResponse> response = feedUseCase.execute(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registrar interacción con un parche",
            description = "Registra una acción del usuario sobre un parche y actualiza su score de afinidad por categoría " +
                    "usando la fórmula de decaimiento temporal: S = S_actual × e^(-0.01 × días) + peso_evento. " +
                    "Pesos por acción — VIEW: +0.1 | JOIN: +1.0 | SKIP: -10.0. " +
                    "El score por categoría se mantiene entre 0 y 100 y determina qué aparece en el feed y recomendaciones. " +
                    "JOIN además excluye el parche de futuras recomendaciones."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Interacción registrada y score de categoría actualizado"),
            @ApiResponse(responseCode = "400", description = "Acción inválida o patchId no encontrado"),
            @ApiResponse(responseCode = "401", description = "JWT inválido o ausente")
    })
    @PostMapping("/parches/{patchId}/interact")
    public ResponseEntity<Void> interact(
            @Parameter(description = "ID del parche con el que se interactúa", required = true)
            @PathVariable UUID patchId,
            @Parameter(description = "ID del usuario autenticado", required = true)
            @RequestParam UUID userId,
            @Valid @RequestBody InteractRequest request) {

        registerInteraction.register(userId, patchId, request.getAction());
        return ResponseEntity.noContent().build();
    }
}
