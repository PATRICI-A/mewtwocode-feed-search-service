package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import org.springframework.stereotype.Component;

@Component
public class PatchEntityMapper {

    public Patch toDomain(PatchEntity entity) {
        return Patch.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .location(entity.getLocation())
                .campusZone(entity.getCampusZone())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .capacity(entity.getCapacity())
                .currentCount(entity.getCurrentCount())
                .status(entity.getStatus())
                .creatorId(entity.getCreatorId())
                .isPublic(entity.getIsPublic())
                .createdTime(entity.getCreatedAt())
                .build();
    }

    public UserInterest toDomain(UserInterestEntity entity) {
        return UserInterest.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .interestingTag(entity.getInterestTag())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public FeedInteraction toDomain(FeedInteractionEntity entity) {
        return FeedInteraction.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .patchId(entity.getPatchId())
                .action(entity.getAction())
                .interactedAt(entity.getInteractedAt())
                .build();
    }
}
