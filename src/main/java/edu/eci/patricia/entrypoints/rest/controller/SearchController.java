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

/**
 * REST controller that exposes the patch search and filtering endpoint under {@code /api/v1/parches}.
 *
 * <p>Supports dynamic, multi-criteria search of public patches with optional keyword matching,
 * category and campus-zone filters, date range constraints, and capacity checks.
 * Results are paginated and include a total count for client-side pagination controls.</p>
 */
@RestController
@RequestMapping("/api/v1/parches")
@Tag(name = "Search", description = "Patch search and filtering")
public class SearchController {

    private final SearchPatchesUseCase searchPatchesUseCase;

    /**
     * Constructs the controller with its required use-case dependency.
     *
     * @param searchPatchesUseCase the use case that executes the search query and returns results
     */
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
    /**
     * Executes a dynamic search against the public patch catalogue and returns a paginated result.
     *
     * @param userId  optional UUID of the authenticated user; when provided, the response includes
     *                membership indicators showing whether the user has already joined each patch
     * @param request the search filter object populated from query parameters
     *                (keyword, category, campus zone, dates, capacity, etc.)
     * @param page    zero-based page index (defaults to 0)
     * @param size    number of items per page (defaults to 20)
     * @return a 200 response containing a {@link SearchResponse} with results and total count
     */
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
