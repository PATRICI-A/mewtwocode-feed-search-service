package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface SearchPatchesUseCase {
    List<PatchSummaryResponse> execute(UUID userId, SearchRequest request, int page, int size);
}
