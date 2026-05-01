package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.ports.in.SearchPatchesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            description = "Búsqueda dinámica de parches por texto, categoría, ubicación y fechas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/search")
    public ResponseEntity<List<PatchSummaryResponse>> search(
            @RequestParam UUID userId,
            SearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<PatchSummaryResponse> response = searchPatchesUseCase.execute(userId, request, page, size);
        return ResponseEntity.ok(response);
    }
}
