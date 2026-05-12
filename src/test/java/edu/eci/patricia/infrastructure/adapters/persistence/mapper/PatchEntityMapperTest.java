package edu.eci.patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PatchEntityMapperTest {

    private final PatchEntityMapper mapper = new PatchEntityMapper();

    @Test
    void toDomainMapeaPatchEntity() {
        UUID id = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(1);
        LocalDateTime createdAt = LocalDateTime.now();
        PatchEntity entity = PatchEntity.builder()
                .id(id)
                .title("Cafe")
                .description("Charla")
                .category(PatchCategory.FOOD)
                .location("Bloque A")
                .campusZone(CampusZone.CAFETERIA)
                .startTime(start)
                .endTime(end)
                .capacity(8)
                .currentCount(3)
                .status(PatchStatus.OPEN)
                .creatorId(creatorId)
                .isPublic(true)
                .createdAt(createdAt)
                .build();

        Patch patch = mapper.toDomain(entity);

        assertThat(patch.getId()).isEqualTo(id);
        assertThat(patch.getTitle()).isEqualTo("Cafe");
        assertThat(patch.getLocation()).isEqualTo("Bloque A");
        assertThat(patch.getCategory()).isEqualTo(PatchCategory.FOOD);
        assertThat(patch.getCampusZone()).isEqualTo(CampusZone.CAFETERIA);
        assertThat(patch.getStartTime()).isEqualTo(start);
        assertThat(patch.getEndTime()).isEqualTo(end);
        assertThat(patch.getCapacity()).isEqualTo(8);
        assertThat(patch.getCurrentCount()).isEqualTo(3);
        assertThat(patch.getStatus()).isEqualTo(PatchStatus.OPEN);
        assertThat(patch.getCreatorId()).isEqualTo(creatorId);
        assertThat(patch.getIsPublic()).isTrue();
        assertThat(patch.getCreatedTime()).isEqualTo(createdAt);
    }

    @Test
    void toDomainMapeaUserInterestEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        UserInterestEntity entity = UserInterestEntity.builder()
                .id(id)
                .userId(userId)
                .interestTag("ajedrez")
                .createdAt(createdAt)
                .build();

        UserInterest interest = mapper.toDomain(entity);

        assertThat(interest.getId()).isEqualTo(id);
        assertThat(interest.getUserId()).isEqualTo(userId);
        assertThat(interest.getInterestingTag()).isEqualTo("ajedrez");
        assertThat(interest.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void toDomainMapeaFeedInteractionEntity() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID patchId = UUID.randomUUID();
        LocalDateTime interactedAt = LocalDateTime.now();
        FeedInteractionEntity entity = FeedInteractionEntity.builder()
                .id(id)
                .userId(userId)
                .patchId(patchId)
                .action(InteractionType.VIEW)
                .interactedAt(interactedAt)
                .build();

        FeedInteraction interaction = mapper.toDomain(entity);

        assertThat(interaction.getId()).isEqualTo(id);
        assertThat(interaction.getUserId()).isEqualTo(userId);
        assertThat(interaction.getPatchId()).isEqualTo(patchId);
        assertThat(interaction.getAction()).isEqualTo(InteractionType.VIEW);
        assertThat(interaction.getInteractedAt()).isEqualTo(interactedAt);
    }
}
