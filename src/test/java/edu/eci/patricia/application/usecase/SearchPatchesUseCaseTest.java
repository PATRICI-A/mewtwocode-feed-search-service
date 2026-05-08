package edu.eci.patricia.application.usecase;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.dto.response.SearchResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPatchesUseCaseTest {

    @Mock
    private PatchRepositoryPort patchRepository;

    @Mock
    private MembershipRepositoryPort membershipRepository;

    @Mock
    private PatchDomainMapper mapper;

    @InjectMocks
    private SearchPatchesUseCaseImpl searchPatchesUseCase;

    private UUID userId;
    private Patch samplePatch;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        samplePatch = Patch.builder()
                .id(UUID.randomUUID())
                .title("Torneo de Futbol")
                .description("Ven a jugar futbol")
                .category(PatchCategory.SPORTS)
                .campusZone(CampusZone.CANCHA)
                .status(PatchStatus.OPEN)
                .isPublic(true)
                .startTime(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void execute_ShouldReturnFilteredPatches() {
        SearchRequest request = SearchRequest.builder()
                .q("Futbol")
                .category(PatchCategory.SPORTS)
                .build();

        PatchSummaryResponse responseDto = PatchSummaryResponse.builder()
                .id(samplePatch.getId())
                .title(samplePatch.getTitle())
                .build();

        when(patchRepository.countPatches(any(SearchRequest.class))).thenReturn(1L);
        when(patchRepository.searchPatches(any(SearchRequest.class), anyInt(), anyInt()))
                .thenReturn(List.of(samplePatch));
        when(membershipRepository.findActivePatchIds(eq(userId), anyList()))
                .thenReturn(Set.of());
        when(mapper.toResponse(eq(samplePatch), anyBoolean(), any())).thenReturn(responseDto);

        SearchResponse result = searchPatchesUseCase.execute(userId, request, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        assertEquals("Torneo de Futbol", result.getResults().get(0).getTitle());
    }

    @Test
    void execute_WithEmptyResults_ShouldReturnEmptyList() {
        SearchRequest request = SearchRequest.builder().q("NonExistent").build();
        when(patchRepository.countPatches(any(SearchRequest.class))).thenReturn(0L);
        when(patchRepository.searchPatches(any(SearchRequest.class), anyInt(), anyInt()))
                .thenReturn(List.of());

        SearchResponse result = searchPatchesUseCase.execute(userId, request, 0, 10);

        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void execute_WithNullUserId_ShouldReturnResultsWithoutMembership() {
        SearchRequest request = SearchRequest.builder().q("Futbol").build();

        PatchSummaryResponse responseDto = PatchSummaryResponse.builder()
                .id(samplePatch.getId())
                .title(samplePatch.getTitle())
                .build();

        when(patchRepository.countPatches(any(SearchRequest.class))).thenReturn(1L);
        when(patchRepository.searchPatches(any(SearchRequest.class), anyInt(), anyInt()))
                .thenReturn(List.of(samplePatch));
        when(mapper.toResponse(eq(samplePatch), eq(false), any())).thenReturn(responseDto);

        SearchResponse result = searchPatchesUseCase.execute(null, request, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
    }
}
