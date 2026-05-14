package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import org.springframework.stereotype.Component;

/**
 * Spring component responsible for bidirectional mapping between JPA persistence entities
 * and domain model objects for patches, user interests, and feed interactions.
 *
 * <p>Centralises all entity-to-domain and domain-to-entity conversions so that adapters
 * remain free of mapping logic.</p>
 */
@Component
public class PatchEntityMapper {

    /**
     * Converts a {@link PatchEntity} persistence entity to a {@link Patch} domain object.
     *
     * @param entity the entity to convert; must not be {@code null}
     * @return the corresponding {@link Patch} domain object
     */
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

    /**
     * Converts a {@link Patch} domain object to a {@link PatchEntity} persistence entity.
     *
     * @param patch the domain object to convert; must not be {@code null}
     * @return the corresponding {@link PatchEntity} ready for persistence
     */
    public PatchEntity toEntity(Patch patch) {
        return PatchEntity.builder()
                .id(patch.getId())
                .title(patch.getTitle())
                .description(patch.getDescription())
                .category(patch.getCategory())
                .location(patch.getLocation())
                .campusZone(patch.getCampusZone())
                .startTime(patch.getStartTime())
                .endTime(patch.getEndTime())
                .capacity(patch.getCapacity())
                .currentCount(patch.getCurrentCount())
                .status(patch.getStatus())
                .creatorId(patch.getCreatorId())
                .isPublic(patch.getIsPublic())
                .createdAt(patch.getCreatedTime())
                .build();
    }

    /**
     * Converts a {@link UserInterestEntity} persistence entity to a {@link UserInterest} domain object.
     *
     * @param entity the entity to convert; must not be {@code null}
     * @return the corresponding {@link UserInterest} domain object
     */
    public UserInterest toDomain(UserInterestEntity entity) {
        return UserInterest.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .interestingTag(entity.getInterestTag())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converts a {@link FeedInteractionEntity} persistence entity to a {@link FeedInteraction} domain object.
     *
     * @param entity the entity to convert; must not be {@code null}
     * @return the corresponding {@link FeedInteraction} domain object
     */
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
