package edu.eci.patricia.application.dto.response;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.model.enums.CampusZone;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PatchSummaryResponse{
    private UUID id;
    private String title;
    private String description;
    private PatchCategory category;
    private CampusZone campusZone;
    private LocalDateTime startTime;
    private Integer capacity;
    private Integer currentCount;
    private PatchStatus status;
    private Boolean isPublic;
    private String creatorName;
    private Float affinityScore;
    private Boolean userIsMember;

}