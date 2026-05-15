package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserCategoryScoreRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterInteractionServiceTest {

    @Mock private FeedInteractionRepositoryPort interactionRepository;
    @Mock private PatchRepositoryPort patchRepository;
    @Mock private UserCategoryScoreRepositoryPort categoryScoreRepository;

    private RegisterInteractionService service;

    private final UUID userId = UUID.randomUUID();
    private final UUID patchId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new RegisterInteractionService(interactionRepository, patchRepository, categoryScoreRepository);
    }

    @Test
    void registerGuardaInteraccionYCreaScoreCuandoNoExiste() {
        when(patchRepository.findByIds(List.of(patchId))).thenReturn(List.of(patch(PatchCategory.SPORTS)));
        when(categoryScoreRepository.findByUserIdAndCategory(userId, PatchCategory.SPORTS))
                .thenReturn(Optional.empty());

        service.register(userId, patchId, InteractionType.JOIN);

        verify(interactionRepository).save(userId, patchId, InteractionType.JOIN);
        ArgumentCaptor<UserCategoryScore> captor = ArgumentCaptor.forClass(UserCategoryScore.class);
        verify(categoryScoreRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
        assertThat(captor.getValue().getCategory()).isEqualTo(PatchCategory.SPORTS);
        assertThat(captor.getValue().getScoreTotal()).isEqualTo(1.0f);
        assertThat(captor.getValue().getLastUpdated()).isNotNull();
    }

    @Test
    void registerActualizaScoreExistenteConDecaimientoYLimiteSuperior() {
        UserCategoryScore existing = UserCategoryScore.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .category(PatchCategory.GAMING)
                .scoreTotal(99.8f)
                .lastUpdated(LocalDateTime.now().minusDays(1))
                .build();
        when(patchRepository.findByIds(List.of(patchId))).thenReturn(List.of(patch(PatchCategory.GAMING)));
        when(categoryScoreRepository.findByUserIdAndCategory(userId, PatchCategory.GAMING))
                .thenReturn(Optional.of(existing));

        service.register(userId, patchId, InteractionType.JOIN);

        ArgumentCaptor<UserCategoryScore> captor = ArgumentCaptor.forClass(UserCategoryScore.class);
        verify(categoryScoreRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(existing);
        assertThat(captor.getValue().getScoreTotal()).isBetween(99.0f, 100.0f);
    }

    @Test
    void registerNoActualizaScoreSiElPatchNoExiste() {
        when(patchRepository.findByIds(List.of(patchId))).thenReturn(List.of());

        service.register(userId, patchId, InteractionType.SKIP);

        verify(interactionRepository).save(userId, patchId, InteractionType.SKIP);
        verify(categoryScoreRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void registerNoPermiteScoreNegativoConSkip() {
        when(patchRepository.findByIds(List.of(patchId))).thenReturn(List.of(patch(PatchCategory.FOOD)));
        when(categoryScoreRepository.findByUserIdAndCategory(userId, PatchCategory.FOOD))
                .thenReturn(Optional.empty());

        service.register(userId, patchId, InteractionType.SKIP);

        ArgumentCaptor<UserCategoryScore> captor = ArgumentCaptor.forClass(UserCategoryScore.class);
        verify(categoryScoreRepository).save(captor.capture());
        assertThat(captor.getValue().getScoreTotal()).isZero();
    }

    private Patch patch(PatchCategory category) {
        return Patch.builder()
                .id(patchId)
                .category(category)
                .build();
    }
}
