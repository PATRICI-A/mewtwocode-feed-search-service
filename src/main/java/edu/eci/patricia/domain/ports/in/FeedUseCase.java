package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import java.util.List;
import java.util.UUID;

public interface FeedUseCase {
    List<PatchSummaryResponse> execute(UUID userId, int page, int size);
}