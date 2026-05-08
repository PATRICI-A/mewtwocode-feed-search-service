package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patch {
    private UUID id;
    private String title;
    private String description;
    private PatchCategory category;
    private String location;
    private CampusZone campusZone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer currentCount;
    private PatchStatus status;
    private UUID creatorId;
    private Boolean isPublic;
    private LocalDateTime createdTime;

    public boolean isFull() {
        return this.currentCount >= this.capacity;
    }

    public boolean isOpen() {
        return this.status == PatchStatus.OPEN;
    }
}
