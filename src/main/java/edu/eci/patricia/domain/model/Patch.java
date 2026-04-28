package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Patch{
    private UUID id;
    private String title;
    private String description;
    private String location;
    private CampusZone campusZone;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Integer currentCount;
    private PatchStatus patchStatus;
    private UUID creatorId;
    private Boolean isPublic;
    private LocalDateTime createdTime;


}