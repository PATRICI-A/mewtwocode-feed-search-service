package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.SearchResponse;

import java.util.UUID;

/**
 * Inbound port (use-case boundary) that exposes the patch search capability.
 * Allows callers to query patches using structured filter criteria with
 * pagination support, returning a response shaped for the API layer.
 */
public interface SearchPatchesUseCase {

    /**
     * Executes a paginated patch search based on the provided filter criteria.
     *
     * @param userId  the unique identifier of the user performing the search,
     *                used to personalise or filter results where applicable
     * @param request the search filter criteria (keywords, category, zone, etc.)
     * @param page    the zero-based page index to retrieve
     * @param size    the maximum number of results per page
     * @return a {@link SearchResponse} containing the matched patches and pagination metadata
     */
    SearchResponse execute(UUID userId, SearchRequest request, int page, int size);
}
