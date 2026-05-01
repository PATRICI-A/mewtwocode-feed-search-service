package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock private PatchRepositoryPort      patchRepository;
    @Mock private MembershipRepositoryPort membershipRepository;
    @Mock private PatchDomainMapper        mapper;

    private FeedService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new FeedService(patchRepository, membershipRepository, mapper);
    }

    @Test
    void retornaListaVaciaCuandoNoHayParches() {
        when(patchRepository.findOpenPublicPatches()).thenReturn(Collections.emptyList());

        List<PatchSummaryResponse> result = service.execute(userId, 0, 20);

        assertThat(result).isEmpty();
    }

    @Test
    void retornaSoloParChesActivosYPublicos() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);
        PatchSummaryResponse response = buildResponse(patch);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenReturn(response);

        List<PatchSummaryResponse> result = service.execute(userId, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PatchStatus.OPEN);
        assertThat(result.get(0).getIsPublic()).isTrue();
    }

    @Test
    void aplicaPaginacionConSkipYLimit() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.SPORTS));
        }

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, 0, 5);

        assertThat(result).hasSize(5);
    }

    @Test
    void segundaPaginaRetornaElementosCorrectos() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.STUDY));
        }

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, 1, 5);

        assertThat(result).hasSize(5);
    }

    @Test
    void paginaFueraDeRangoRetornaListaVacia() {
        List<Patch> patches = List.of(buildPatch(UUID.randomUUID(), PatchCategory.FOOD));

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, 5, 20);

        assertThat(result).isEmpty();
    }

    @Test
    void indicaCorrectamenteSiUsuarioEsMiembro() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.CULTURE);
        PatchSummaryResponse response = buildResponse(patch);
        response.setUserIsMember(true);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(true);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenReturn(response);

        List<PatchSummaryResponse> result = service.execute(userId, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserIsMember()).isTrue();
    }

    private Patch buildPatch(UUID id, PatchCategory category) {
        return Patch.builder()
                .id(id)
                .title("Test Patch")
                .description("desc")
                .category(category)
                .campusZone(CampusZone.CAFETERIA)
                .startTime(LocalDateTime.now().plusHours(1))
                .capacity(20)
                .currentCount(0)
                .status(PatchStatus.OPEN)
                .isPublic(true)
                .creatorId(UUID.randomUUID())
                .build();
    }

    private PatchSummaryResponse buildResponse(Patch patch) {
        return PatchSummaryResponse.builder()
                .id(patch.getId())
                .title(patch.getTitle())
                .category(patch.getCategory())
                .campusZone(patch.getCampusZone())
                .status(patch.getStatus())
                .isPublic(patch.getIsPublic())
                .capacity(patch.getCapacity())
                .currentCount(patch.getCurrentCount())
                .userIsMember(false)
                .build();
    }
}