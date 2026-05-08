package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ScoredPatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock private PatchRepositoryPort             patchRepository;
    @Mock private UserCategoryScoreRepositoryPort categoryScoreRepository;
    @Mock private FeedInteractionRepositoryPort   interactionRepository;

    private RecommendationService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new RecommendationService(patchRepository, categoryScoreRepository, interactionRepository);
    }

    @Test
    void returnsEmptyListWhenNoCandidates() {
        when(categoryScoreRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void highScoreWhenCategoryMatchesUserScore() {
        // catScore = 40 → 40/100 = 0.40
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.GAMING, 40f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.4f);
        assertThat(result.get(0).getReason()).contains("profile");
    }

    @Test
    void excludesPatchesWhereUserJoined() {
        UUID patchId = UUID.randomUUID();
        Patch patch = buildPatch(patchId, PatchCategory.STUDY);

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.STUDY, 40f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Set.of(patchId));
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void limitsToTenRecommendations() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 15; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.SPORTS));
        }

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.SPORTS, 40f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(10);
    }

    @Test
    void scoreReflectsCategoryScoreValue() {
        // catScore = 30 → 30/100 = 0.30
        Patch candidate = buildPatch(UUID.randomUUID(), PatchCategory.FOOD);

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.FOOD, 30f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(candidate));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.3f);
    }

    @Test
    void excludesPatchesWithZeroOrNegativeScore() {
        // User has score for SPORTS, patch is CULTURE → catScore = 0 → excluded
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.CULTURE);

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.SPORTS, 40f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void lowScoreWithMinimumCategoryScore() {
        // catScore = 15 → 15/100 = 0.15
        Patch candidate = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);

        when(categoryScoreRepository.findByUserId(userId)).thenReturn(
                List.of(buildCategoryScore(PatchCategory.GAMING, 15f)));
        when(interactionRepository.findJoinedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(candidate));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.15f);
        assertThat(result.get(0).getReason()).contains("profile");
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

    private UserCategoryScore buildCategoryScore(PatchCategory category, float score) {
        return UserCategoryScore.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .category(category)
                .scoreTotal(score)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
