package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.SearchResponse;
import edu.eci.patricia.domain.ports.in.SearchPatchesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parches")
@Tag(name = "Search", description = "Búsqueda y filtrado de parches")
public class SearchController {

    private final SearchPatchesUseCase searchPatchesUseCase;

    public SearchController(SearchPatchesUseCase searchPatchesUseCase) {
        this.searchPatchesUseCase = searchPatchesUseCase;
    }

    @Operation(
            summary = "Buscar parches con filtros",
            description = "Búsqueda dinámica de parches públicos. El campo 'q' filtra por título y descripción " +
                    "(mínimo 2 caracteres si se provee). Los demás filtros son opcionales y acumulables."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "400", description = "Parámetro inválido (ej. 'q' con menos de 2 caracteres)"),
            @ApiResponse(responseCode = "401", description = "JWT inválido o ausente")
    })
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "ID del usuario que realiza la búsqueda (opcional, activa indicador de membresía)")
            @RequestParam(required = false) UUID userId,
            @Valid SearchRequest request,
            @Parameter(description = "Número de página (base 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Resultados por página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        SearchResponse response = searchPatchesUseCase.execute(userId, request, page, size);
        return ResponseEntity.ok(response);
    }
}
