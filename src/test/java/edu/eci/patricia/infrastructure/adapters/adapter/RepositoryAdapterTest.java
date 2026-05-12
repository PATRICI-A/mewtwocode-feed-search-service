package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserCategoryScore;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserCategoryScoreEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.UserInterestEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.FeedInteractionJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchMembershipJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserCategoryScoreJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserInterestJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryAdapterTest {

    @Mock private PatchJpaRepository patchJpaRepository;
    @Mock private FeedInteractionJpaRepository feedInteractionJpaRepository;
    @Mock private PatchMembershipJpaRepository membershipJpaRepository;
    @Mock private UserInterestJpaRepository userInterestJpaRepository;
    @Mock private UserCategoryScoreJpaRepository scoreJpaRepository;

    private final PatchEntityMapper mapper = new PatchEntityMapper();

    @Test
    void patchRepositoryBuscaPatchesAbiertosPublicos() {
        PatchEntity entity = patchEntity(UUID.randomUUID(), PatchCategory.SPORTS);
        when(patchJpaRepository.findByStatusAndIsPublic(PatchStatus.OPEN, true)).thenReturn(List.of(entity));
        PatchRepositoryAdapter adapter = new PatchRepositoryAdapter(patchJpaRepository, mapper);

        List<Patch> result = adapter.findOpenPublicPatches();

        assertThat(result).extracting(Patch::getId).containsExactly(entity.getId());
        assertThat(result).extracting(Patch::getCategory).containsExactly(PatchCategory.SPORTS);
    }

    @Test
    void patchRepositoryBuscaPorIdsYPopularesConLimite() {
        PatchEntity first = patchEntity(UUID.randomUUID(), PatchCategory.GAMING);
        PatchEntity second = patchEntity(UUID.randomUUID(), PatchCategory.FOOD);
        PatchRepositoryAdapter adapter = new PatchRepositoryAdapter(patchJpaRepository, mapper);
        when(patchJpaRepository.findAllById(List.of(first.getId(), second.getId()))).thenReturn(List.of(first, second));
        when(patchJpaRepository.findTop10ByStatusAndIsPublicOrderByCurrentCountDesc(PatchStatus.OPEN, true))
                .thenReturn(List.of(first, second));

        assertThat(adapter.findByIds(List.of(first.getId(), second.getId()))).hasSize(2);
        assertThat(adapter.findPopularPatches(1)).extracting(Patch::getId).containsExactly(first.getId());
    }

    @Test
    void patchRepositoryBuscaYCuentaConSpecification() {
        PatchEntity entity = patchEntity(UUID.randomUUID(), PatchCategory.CULTURE);
        PatchRepositoryAdapter adapter = new PatchRepositoryAdapter(patchJpaRepository, mapper);
        when(patchJpaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(patchJpaRepository.count(any(Specification.class))).thenReturn(7L);

        SearchRequest request = SearchRequest.builder().q("cine").build();

        assertThat(adapter.searchPatches(request, 1, 5)).extracting(Patch::getId).containsExactly(entity.getId());
        assertThat(adapter.countPatches(request)).isEqualTo(7L);
    }

    @Test
    void feedInteractionRepositoryMapeaConsultasYGuardaInteraccion() {
        UUID userId = UUID.randomUUID();
        UUID patchId = UUID.randomUUID();
        FeedInteractionEntity entity = FeedInteractionEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .patchId(patchId)
                .action(InteractionType.JOIN)
                .interactedAt(LocalDateTime.now())
                .build();
        when(feedInteractionJpaRepository.findByUserId(userId)).thenReturn(List.of(entity));
        when(feedInteractionJpaRepository.findJoinedPatchIdsByUserId(userId)).thenReturn(List.of(patchId, patchId));
        FeedInteractionRepositoryAdapter adapter = new FeedInteractionRepositoryAdapter(feedInteractionJpaRepository, mapper);

        List<FeedInteraction> interactions = adapter.findByUserId(userId);
        Set<UUID> joined = adapter.findJoinedPatchIds(userId);
        adapter.save(userId, patchId, InteractionType.VIEW);

        assertThat(interactions).extracting(FeedInteraction::getPatchId).containsExactly(patchId);
        assertThat(joined).containsExactly(patchId);
        ArgumentCaptor<FeedInteractionEntity> captor = ArgumentCaptor.forClass(FeedInteractionEntity.class);
        verify(feedInteractionJpaRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(InteractionType.VIEW);
        assertThat(captor.getValue().getInteractedAt()).isNotNull();
    }

    @Test
    void membershipRepositoryConsultaMembresiasActivas() {
        UUID userId = UUID.randomUUID();
        UUID patchId = UUID.randomUUID();
        MembershipRepositoryAdapter adapter = new MembershipRepositoryAdapter(membershipJpaRepository);
        when(membershipJpaRepository.existsByPatchIdAndUserIdAndStatus(patchId, userId, MembershipStatus.ACTIVE))
                .thenReturn(true);
        when(membershipJpaRepository.findActivePatchIdsByUserIdAndPatchIds(userId, List.of(patchId)))
                .thenReturn(Set.of(patchId));

        assertThat(adapter.existsActiveMembership(patchId, userId)).isTrue();
        assertThat(adapter.findActivePatchIds(userId, List.of())).isEmpty();
        assertThat(adapter.findActivePatchIds(userId, List.of(patchId))).containsExactly(patchId);
        verify(membershipJpaRepository, never()).findActivePatchIdsByUserIdAndPatchIds(eq(userId), eq(List.of()));
    }

    @Test
    void userInterestRepositoryMapeaIntereses() {
        UUID userId = UUID.randomUUID();
        UserInterestEntity entity = UserInterestEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .interestTag("musica")
                .createdAt(LocalDateTime.now())
                .build();
        when(userInterestJpaRepository.findByUserId(userId)).thenReturn(List.of(entity));
        UserInterestRepositoryAdapter adapter = new UserInterestRepositoryAdapter(userInterestJpaRepository, mapper);

        List<UserInterest> result = adapter.findByUserId(userId);

        assertThat(result).extracting(UserInterest::getInterestingTag).containsExactly("musica");
    }

    @Test
    void userCategoryScoreRepositoryMapeaConsultasYGuardado() {
        UUID userId = UUID.randomUUID();
        UserCategoryScoreEntity entity = UserCategoryScoreEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .category(PatchCategory.STUDY)
                .scoreTotal(12f)
                .lastUpdated(LocalDateTime.now())
                .build();
        when(scoreJpaRepository.findByUserId(userId)).thenReturn(List.of(entity));
        when(scoreJpaRepository.findByUserIdAndCategory(userId, PatchCategory.STUDY)).thenReturn(Optional.of(entity));
        when(scoreJpaRepository.save(any(UserCategoryScoreEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserCategoryScoreRepositoryAdapter adapter = new UserCategoryScoreRepositoryAdapter(scoreJpaRepository);

        List<UserCategoryScore> scores = adapter.findByUserId(userId);
        Optional<UserCategoryScore> found = adapter.findByUserIdAndCategory(userId, PatchCategory.STUDY);
        UserCategoryScore saved = adapter.save(scores.get(0));

        assertThat(scores).extracting(UserCategoryScore::getScoreTotal).containsExactly(12f);
        assertThat(found).isPresent();
        assertThat(saved.getCategory()).isEqualTo(PatchCategory.STUDY);
    }

    private PatchEntity patchEntity(UUID id, PatchCategory category) {
        return PatchEntity.builder()
                .id(id)
                .title("Patch")
                .description("Descripcion")
                .category(category)
                .startTime(LocalDateTime.now().plusHours(1))
                .capacity(10)
                .currentCount(2)
                .status(PatchStatus.OPEN)
                .creatorId(UUID.randomUUID())
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
