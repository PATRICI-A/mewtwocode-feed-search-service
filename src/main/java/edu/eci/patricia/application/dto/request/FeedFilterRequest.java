package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Optional filters for the personalized feed")
public class FeedFilterRequest {

    @Schema(description = "Filter by patch category", example = "SPORTS")
    private PatchCategory category;

    @Schema(description = "Filter by campus zone", example = "CAFETERIA")
    private CampusZone campusZone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Filter patches starting from this date (inclusive)", example = "2025-06-01")
    private LocalDate dateFrom;
}
