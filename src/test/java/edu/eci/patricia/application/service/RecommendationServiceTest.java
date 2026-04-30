package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock private PatchRepositoryPort patchRepository;
    @Mock private UserInterestRepositoryPort interestRepository;
    @Mock private FeedInteractionRepositoryPort interactionRepository;

    private RecommendationService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new RecommendationService(patchRepository, interestRepository, interactionRepository);
    }

    @Test
    void retornaListaVaciaCuandoNoHayCandidatos() {
        when(interestRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(Collections.emptyList());

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void puntajeAltoSiCategoriaCoincideConInteresDelUsuario() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.GAMING);

        when(interestRepository.findByUserId(userId)).thenReturn(List.of(buildInterest("GAMING")));
        when(interactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.4f);
        assertThat(result.get(0).getReason()).contains("interests");
    }

    @Test
    void excluyePatchesConLosQueElUsuarioYaInteractuo() {
        UUID patchId = UUID.randomUUID();
        Patch patch = buildPatch(patchId, PatchCategory.STUDY);

        when(interestRepository.findByUserId(userId)).thenReturn(List.of(buildInterest("STUDY")));
        when(interactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Set.of(patchId));
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));
        when(patchRepository.findByIds(any())).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void limiteMaximoDiezRecomendaciones() {
        List<Patch> patches = new java.util.ArrayList<>();
        for (int i = 0; i < 15; i++) {
            patches.add(buildPatch(UUID.randomUUID(), PatchCategory.SPORTS));
        }

        when(interestRepository.findByUserId(userId)).thenReturn(List.of(buildInterest("SPORTS")));
        when(interactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(patches);

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).hasSize(10);
    }

    @Test
    void aplicaPenalizacionPorSkipEnMismaCategoria() {
        UUID patchInteractuado = UUID.randomUUID();
        UUID patchCandidato = UUID.randomUUID();

        Patch patchVisto = buildPatch(patchInteractuado, PatchCategory.FOOD);
        Patch candidato = buildPatch(patchCandidato, PatchCategory.FOOD);

        FeedInteraction skip = FeedInteraction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .patchId(patchInteractuado)
                .action(InteractionType.SKIP)
                .interactedAt(LocalDateTime.now())
                .build();

        when(interestRepository.findByUserId(userId)).thenReturn(List.of(buildInterest("FOOD")));
        when(interactionRepository.findByUserId(userId)).thenReturn(List.of(skip));
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Set.of(patchInteractuado));
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(candidato));
        when(patchRepository.findByIds(any())).thenReturn(List.of(patchVisto));

        List<ScoredPatch> result = service.getRecommendations(userId);

        // 0.4 (interest match) - 0.1 (un skip) = 0.3
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.3f);
    }

    @Test
    void excluyePatchesConScoreCeroONegativo() {
        Patch patch = buildPatch(UUID.randomUUID(), PatchCategory.CULTURE);

        when(interestRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Collections.emptySet());
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(patch));

        List<ScoredPatch> result = service.getRecommendations(userId);

        assertThat(result).isEmpty();
    }

    @Test
    void sumaScorePorJoinEnMismaCategoria() {
        UUID patchInteractuado = UUID.randomUUID();
        UUID patchCandidato = UUID.randomUUID();

        Patch patchUnido = buildPatch(patchInteractuado, PatchCategory.GAMING);
        Patch candidato = buildPatch(patchCandidato, PatchCategory.GAMING);

        FeedInteraction join = FeedInteraction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .patchId(patchInteractuado)
                .action(InteractionType.JOIN)
                .interactedAt(LocalDateTime.now())
                .build();

        when(interestRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(interactionRepository.findByUserId(userId)).thenReturn(List.of(join));
        when(interactionRepository.findInteractedPatchIds(userId)).thenReturn(Set.of(patchInteractuado));
        when(patchRepository.findOpenPublicPatches()).thenReturn(List.of(candidato));
        when(patchRepository.findByIds(any())).thenReturn(List.of(patchUnido));

        List<ScoredPatch> result = service.getRecommendations(userId);

        // 0.15 por un join en la misma categoria
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffinityScore()).isEqualTo(0.15f);
        assertThat(result.get(0).getReason()).contains("joined");
    }

    // --- helpers ---

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

    private UserInterest buildInterest(String tag) {
        return UserInterest.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .interestingTag(tag)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
