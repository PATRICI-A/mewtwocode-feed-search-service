package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.request.FeedFilterRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port (use-case boundary) that exposes the personalised feed of patches
 * for a given user. Applies optional filter criteria and returns a paginated list
 * of patch summaries suitable for display in the feed view.
 */
public interface FeedUseCase {

    /**
     * Returns a paginated, personalised feed of patches for the given user,
     * optionally filtered by the provided criteria.
     *
     * @param userId  the unique identifier of the user whose feed is being requested
     * @param filters optional filter parameters to narrow the feed results (may be {@code null})
     * @param page    the zero-based page index to retrieve
     * @param size    the maximum number of patch summaries per page
     * @return an ordered list of {@link PatchSummaryResponse} objects for the requested page;
     *         never {@code null}, may be empty
     */
    List<PatchSummaryResponse> execute(UUID userId, FeedFilterRequest filters, int page, int size);
}
