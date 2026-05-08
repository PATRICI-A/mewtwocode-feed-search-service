package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.request.FeedFilterRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Mock private PatchRepositoryPort             patchRepository;
    @Mock private UserCategoryScoreRepositoryPort categoryScoreRepository;
    @Mock private MembershipRepositoryPort        membershipRepository;
    @Mock private PatchDomainMapper               mapper;

    private FeedService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new FeedService(patchRepository, categoryScoreRepository, membershipRepository, mapper);
        when(categoryScoreRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
    }

    @Test
    void returnsEmptyListWhenNoPatches() {
        when(patchRepository.findOpenPublicPatches()).thenReturn(Collections.emptyList());

        List<PatchSummaryResponse> result = service.execute(userId, null, 0, 20);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsOnlyOpenAndPublicPatches() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);
        PatchSummaryResponse response = buildResponse(patch);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenReturn(response);

        List<PatchSummaryResponse> result = service.execute(userId, null, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PatchStatus.OPEN);
        assertThat(result.get(0).getIsPublic()).isTrue();
    }

    @Test
    void appliesPaginationWithSkipAndLimit() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.SPORTS));
        }

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, null, 0, 5);

        assertThat(result).hasSize(5);
    }

    @Test
    void secondPageReturnsCorrectElements() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.STUDY));
        }

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, null, 1, 5);

        assertThat(result).hasSize(5);
    }

    @Test
    void outOfRangePageReturnsEmptyList() {
        List<Patch> patches = List.of(buildPatch(UUID.randomUUID(), PatchCategory.FOOD));

        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        List<PatchSummaryResponse> result = service.execute(userId, null, 5, 20);

        assertThat(result).isEmpty();
    }

    @Test
    void correctlyIndicatesUserMembership() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.CULTURE);
        PatchSummaryResponse response = buildResponse(patch);
        response.setUserIsMember(true);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(true);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenReturn(response);

        List<PatchSummaryResponse> result = service.execute(userId, null, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserIsMember()).isTrue();
    }

    @Test
    void categoryFilterNarrowsResults() {
        Patch sports = buildPatch(UUID.randomUUID(), PatchCategory.SPORTS);
        Patch gaming = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(sports, gaming));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        FeedFilterRequest filters = FeedFilterRequest.builder().category(PatchCategory.SPORTS).build();
        List<PatchSummaryResponse> result = service.execute(userId, filters, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(PatchCategory.SPORTS);
    }

    @Test
    void campusZoneFilterNarrowsResults() {
        Patch cafeteria = buildPatchWithZone(UUID.randomUUID(), PatchCategory.FOOD, CampusZone.CAFETERIA);
        Patch cancha    = buildPatchWithZone(UUID.randomUUID(), PatchCategory.SPORTS, CampusZone.CANCHA);

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(cafeteria, cancha));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        FeedFilterRequest filters = FeedFilterRequest.builder().campusZone(CampusZone.CAFETERIA).build();
        List<PatchSummaryResponse> result = service.execute(userId, filters, 0, 20);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCampusZone()).isEqualTo(CampusZone.CAFETERIA);
    }

    @Test
    void dateFromFilterExcludesPastPatches() {
        Patch future = buildPatchWithDate(UUID.randomUUID(), PatchCategory.GAMING,
                LocalDateTime.now().plusDays(5));
        Patch past = buildPatchWithDate(UUID.randomUUID(), PatchCategory.GAMING,
                LocalDateTime.now().minusDays(1));

        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(future, past));
        when(membershipRepository.existsActiveMembership(any(), any())).thenReturn(false);
        when(mapper.toResponse(any(), anyBoolean(), any())).thenAnswer(inv ->
                buildResponse(inv.getArgument(0)));

        FeedFilterRequest filters = FeedFilterRequest.builder()
                .dateFrom(LocalDate.now())
                .build();
        List<PatchSummaryResponse> result = service.execute(userId, filters, 0, 20);

        assertThat(result).hasSize(1);
    }

    private Patch buildPatch(UUID id, PatchCategory category) {
        return buildPatchWithZone(id, category, CampusZone.CAFETERIA);
    }

    private Patch buildPatchWithZone(UUID id, PatchCategory category, CampusZone zone) {
        return Patch.builder()
                .id(id)
                .title("Test Patch")
                .description("desc")
                .category(category)
                .campusZone(zone)
                .startTime(LocalDateTime.now().plusHours(1))
                .capacity(20)
                .currentCount(0)
                .status(PatchStatus.OPEN)
                .isPublic(true)
                .creatorId(UUID.randomUUID())
                .build();
    }

    private Patch buildPatchWithDate(UUID id, PatchCategory category, LocalDateTime startTime) {
        return Patch.builder()
                .id(id)
                .title("Test Patch")
                .description("desc")
                .category(category)
                .campusZone(CampusZone.CAFETERIA)
                .startTime(startTime)
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
