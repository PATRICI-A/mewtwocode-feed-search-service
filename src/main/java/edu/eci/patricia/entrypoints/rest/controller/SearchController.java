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
@Tag(name = "Search", description = "Patch search and filtering")
public class SearchController {

    private final SearchPatchesUseCase searchPatchesUseCase;

    public SearchController(SearchPatchesUseCase searchPatchesUseCase) {
        this.searchPatchesUseCase = searchPatchesUseCase;
    }

    @Operation(
            summary = "Search patches with filters",
            description = "Dynamic search of public patches. Field 'q' filters by title and description " +
                    "(minimum 2 characters if provided). All other filters are optional and stackable."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter (e.g. 'q' with less than 2 characters)"),
            @ApiResponse(responseCode = "401", description = "Invalid or missing JWT")
    })
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "ID of the user performing the search (optional, enables membership indicator)")
            @RequestParam(required = false) UUID userId,
            @Valid SearchRequest request,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Results per page", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        SearchResponse response = searchPatchesUseCase.execute(userId, request, page, size);
        return ResponseEntity.ok(response);
    }
}
