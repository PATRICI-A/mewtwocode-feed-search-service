package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.response.PatchRecommendationResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.ports.in.GetRecommendationsPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Recomendaciones personalizadas de parches")
public class RecommendationController {

    private final GetRecommendationsPort getRecommendationsPort;
    private final PatchDomainMapper mapper;

    public RecommendationController(GetRecommendationsPort getRecommendationsPort, PatchDomainMapper mapper) {
        this.getRecommendationsPort = getRecommendationsPort;
        this.mapper = mapper;
    }

    @Operation(summary = "Obtener recomendaciones de parches",
               description = "Retorna hasta 10 parches recomendados para el usuario según sus intereses e historial de actividad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de recomendaciones (puede ser vacía)"),
        @ApiResponse(responseCode = "401", description = "JWT inválido o ausente"),
        @ApiResponse(responseCode = "503", description = "Servicio dependiente no disponible")
    })
    @GetMapping("/recommended")
    public ResponseEntity<List<PatchRecommendationResponse>> getRecommendations(
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
