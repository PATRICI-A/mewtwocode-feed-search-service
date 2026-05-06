package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatchSpecificationTest {

    @Test
    void fromRequestNuncaRetornaNull() {
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(SearchRequest.builder().build());
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConTerminoQRetornaSpec() {
        SearchRequest request = SearchRequest.builder().q("futbol").build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConCategoriaRetornaSpec() {
        SearchRequest request = SearchRequest.builder().category(PatchCategory.SPORTS).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConCampusZoneRetornaSpec() {
        SearchRequest request = SearchRequest.builder().campusZone(CampusZone.CAFETERIA).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConStatusRetornaSpec() {
        SearchRequest request = SearchRequest.builder().status(PatchStatus.OPEN).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConDateFromRetornaSpec() {
        SearchRequest request = SearchRequest.builder()
                .dateFrom(LocalDate.of(2025, 6, 1))
                .build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConDateToRetornaSpec() {
        SearchRequest request = SearchRequest.builder()
                .dateTo(LocalDate.of(2025, 6, 30))
                .build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConMaxGroupSizeRetornaSpec() {
        SearchRequest request = SearchRequest.builder().maxGroupSize(10).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConHasAvailableSpotsRetornaSpec() {
        SearchRequest request = SearchRequest.builder().hasAvailableSpots(true).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestConTodosFiltrosRetornaSpec() {
        SearchRequest request = SearchRequest.builder()
                .q("parche")
                .category(PatchCategory.GAMING)
                .campusZone(CampusZone.BIBLIOTECA)
                .status(PatchStatus.OPEN)
                .dateFrom(LocalDate.of(2025, 1, 1))
                .dateTo(LocalDate.of(2025, 12, 31))
                .maxGroupSize(20)
                .hasAvailableSpots(true)
                .build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }
}
