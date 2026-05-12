package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PatchDomainMapperTest {

    private final PatchDomainMapper mapper = new PatchDomainMapper();

    @Test
    void toResponseMapeaCamposDelDominio() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        Patch patch = Patch.builder()
                .id(id)
                .title("Torneo")
                .description("Partidas rapidas")
                .category(PatchCategory.GAMING)
                .campusZone(CampusZone.BIBLIOTECA)
                .startTime(start)
                .capacity(12)
                .currentCount(4)
                .status(PatchStatus.OPEN)
                .isPublic(true)
                .build();

        PatchSummaryResponse response = mapper.toResponse(patch, true, 0.75f);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTitle()).isEqualTo("Torneo");
        assertThat(response.getDescription()).isEqualTo("Partidas rapidas");
        assertThat(response.getCategory()).isEqualTo(PatchCategory.GAMING);
        assertThat(response.getCampusZone()).isEqualTo(CampusZone.BIBLIOTECA);
        assertThat(response.getStartTime()).isEqualTo(start);
        assertThat(response.getCapacity()).isEqualTo(12);
        assertThat(response.getCurrentCount()).isEqualTo(4);
        assertThat(response.getStatus()).isEqualTo(PatchStatus.OPEN);
        assertThat(response.getIsPublic()).isTrue();
        assertThat(response.getCreatorName()).isEqualTo("Usuario desconocido");
        assertThat(response.getAffinityScore()).isEqualTo(0.75f);
        assertThat(response.getUserIsMember()).isTrue();
    }
}
