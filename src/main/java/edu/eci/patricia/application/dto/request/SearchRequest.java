package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object that encapsulates all optional search criteria for the patch search endpoint.
 * Up to eight combinable predicates are supported: free-text query, category, campus zone, status,
 * date range, maximum group size, and available-spots filter.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    @Size(min = 2, message = "Search term must be at least 2 characters")
    @Schema(description = "Text to search in title and description (min 2 characters)", example = "football")
    private String q;

    @Schema(description = "Patch category", example = "SPORTS")
    private PatchCategory category;

    @Schema(description = "Campus zone", example = "CAFETERIA")
    private CampusZone campusZone;

    @Schema(description = "Patch status", example = "OPEN")
    private PatchStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Start date of range (inclusive)", example = "2025-06-01")
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "End date of range (inclusive)", example = "2025-06-30")
    private LocalDate dateTo;

    @Schema(description = "Maximum group capacity", example = "10")
    private Integer maxGroupSize;

    @Schema(description = "Only patches with available spots", example = "true")
    private Boolean hasAvailableSpots;
}
