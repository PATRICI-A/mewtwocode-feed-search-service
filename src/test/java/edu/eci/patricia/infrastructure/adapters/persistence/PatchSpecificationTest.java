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
    void fromRequestNeverReturnsNull() {
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(SearchRequest.builder().build());
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithQueryTermReturnsSpec() {
        SearchRequest request = SearchRequest.builder().q("futbol").build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithCategoryReturnsSpec() {
        SearchRequest request = SearchRequest.builder().category(PatchCategory.SPORTS).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithCampusZoneReturnsSpec() {
        SearchRequest request = SearchRequest.builder().campusZone(CampusZone.CAFETERIA).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithStatusReturnsSpec() {
        SearchRequest request = SearchRequest.builder().status(PatchStatus.OPEN).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithDateFromReturnsSpec() {
        SearchRequest request = SearchRequest.builder()
                .dateFrom(LocalDate.of(2025, 6, 1))
                .build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithDateToReturnsSpec() {
        SearchRequest request = SearchRequest.builder()
                .dateTo(LocalDate.of(2025, 6, 30))
                .build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithMaxGroupSizeReturnsSpec() {
        SearchRequest request = SearchRequest.builder().maxGroupSize(10).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithAvailableSpotsReturnsSpec() {
        SearchRequest request = SearchRequest.builder().hasAvailableSpots(true).build();
        Specification<PatchEntity> spec = PatchSpecification.fromRequest(request);
        assertThat(spec).isNotNull();
    }

    @Test
    void fromRequestWithAllFiltersReturnsSpec() {
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
