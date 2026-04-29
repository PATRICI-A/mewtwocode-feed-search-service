package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.model.Patch;
import org.springframework.stereotype.Component;

@Component
public class PatchApplicationMapper{

    public PatchSummaryResponse toResponse(Patch patch, boolean userIsMember, Float affinityScore){
        return PatchSummaryResponse.builder()
                .id(patch.getId())
                .title(patch.getTitle())
                .description(patch.getDescription())
                .category(patch.getCategory())
                .campusZone(patch.getCampusZone())
                .startTime(patch.getStartTime())
                .capacity(patch.getCapacity())
                .currentCount(patch.getCurrentCount())
                .status(patch.getStatus())
                .isPublic(patch.getIsPublic())
                .creatorName("Usuario desconocido")
                .affinityScore(affinityScore)
                .userIsMember(userIsMember)
                .build();
    }
}