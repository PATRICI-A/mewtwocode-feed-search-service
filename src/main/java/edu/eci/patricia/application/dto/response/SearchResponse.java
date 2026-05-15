package edu.eci.patricia.application.dto.response;

import lombok.*;

import java.util.List;

/**
 * Paginated Data Transfer Object returned by the patch search endpoint.
 * Contains the current page of results together with pagination metadata so clients can
 * navigate through large result sets.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    /** Ordered list of patch summaries for the current page. */
    private List<PatchSummaryResponse> results;
    /** Total number of patches that match the search criteria across all pages. */
    private long total;
    /** Zero-based index of the current page. */
    private int page;
    /** Maximum number of results included in a single page. */
    private int size;
    /** Total number of pages available given the current page size. */
    private int totalPages;
}
