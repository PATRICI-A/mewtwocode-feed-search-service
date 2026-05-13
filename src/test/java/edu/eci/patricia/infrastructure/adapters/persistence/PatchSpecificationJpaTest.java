package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatchSpecificationJpaTest {

    @Autowired
    private PatchJpaRepository repository;

    @Test
    void fromRequestAplicaTodosLosFiltros() {
        PatchEntity expected = savePatch("Ajedrez rapido", "torneo amistoso", PatchCategory.GAMING,
                CampusZone.BIBLIOTECA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 15, 0),
                6, 2);
        savePatch("Futbol", "cancha", PatchCategory.SPORTS,
                CampusZone.CANCHA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 15, 0),
                6, 2);
        savePatch("Ajedrez cerrado", "torneo", PatchCategory.GAMING,
                CampusZone.BIBLIOTECA, PatchStatus.CLOSED, LocalDateTime.of(2026, 5, 20, 15, 0),
                6, 2);
        savePatch("Ajedrez lleno", "torneo", PatchCategory.GAMING,
                CampusZone.BIBLIOTECA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 15, 0),
                2, 2);

        SearchRequest request = SearchRequest.builder()
                .q("ajedrez")
                .category(PatchCategory.GAMING)
                .campusZone(CampusZone.BIBLIOTECA)
                .status(PatchStatus.OPEN)
                .dateFrom(LocalDate.of(2026, 5, 19))
                .dateTo(LocalDate.of(2026, 5, 21))
                .maxGroupSize(6)
                .hasAvailableSpots(true)
                .build();

        List<PatchEntity> result = repository.findAll(PatchSpecification.fromRequest(request));

        assertThat(result).extracting(PatchEntity::getId).containsExactly(expected.getId());
    }

    @Test
    void fromRequestSinFiltrosRetornaTodos() {
        PatchEntity first = savePatch("Cafe", "charla", PatchCategory.FOOD,
                CampusZone.CAFETERIA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 12, 0),
                4, 1);
        PatchEntity second = savePatch("Estudio", "calculo", PatchCategory.STUDY,
                CampusZone.BIBLIOTECA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 21, 12, 0),
                4, 1);

        List<PatchEntity> result = repository.findAll(PatchSpecification.fromRequest(SearchRequest.builder().build()));

        assertThat(result).extracting(PatchEntity::getId).containsExactlyInAnyOrder(first.getId(), second.getId());
    }

    @Test
    void fromRequestFiltraPorTituloODescripcionIgnorandoMayusculas() {
        PatchEntity expected = savePatch("Lectura", "Club de misterio", PatchCategory.CULTURE,
                CampusZone.BIBLIOTECA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 12, 0),
                5, 0);
        savePatch("Musica", "ensayo", PatchCategory.CULTURE,
                CampusZone.BIBLIOTECA, PatchStatus.OPEN, LocalDateTime.of(2026, 5, 20, 12, 0),
                5, 0);

        SearchRequest request = SearchRequest.builder().q("MISTERIO").build();

        List<PatchEntity> result = repository.findAll(PatchSpecification.fromRequest(request));

        assertThat(result).extracting(PatchEntity::getId).containsExactly(expected.getId());
    }

    private PatchEntity savePatch(String title,
                                  String description,
                                  PatchCategory category,
                                  CampusZone campusZone,
                                  PatchStatus status,
                                  LocalDateTime startTime,
                                  int capacity,
                                  int currentCount) {
        return repository.save(PatchEntity.builder()
                .title(title)
                .description(description)
                .category(category)
                .campusZone(campusZone)
                .startTime(startTime)
                .capacity(capacity)
                .currentCount(currentCount)
                .status(status)
                .creatorId(UUID.randomUUID())
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
