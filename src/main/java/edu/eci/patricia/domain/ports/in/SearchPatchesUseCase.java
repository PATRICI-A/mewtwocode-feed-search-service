package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.SearchResponse;

import java.util.UUID;

public interface SearchPatchesUseCase {
    SearchResponse execute(UUID userId, SearchRequest request, int page, int size);
}
