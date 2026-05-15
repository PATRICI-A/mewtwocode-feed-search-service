package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;
import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchMembershipEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchMembershipJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatchWriteRepositoryAdapterTest {

    @Mock private PatchJpaRepository patchRepository;
    @Mock private PatchMembershipJpaRepository membershipRepository;

    private final PatchEntityMapper mapper = new PatchEntityMapper();

    @Test
    void saveMapeaPatchYRetornaDominioPersistido() {
        Patch patch = patch();
        when(patchRepository.save(org.mockito.ArgumentMatchers.any(PatchEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        PatchWriteRepositoryAdapter adapter = new PatchWriteRepositoryAdapter(patchRepository, membershipRepository, mapper);

        Patch saved = adapter.save(patch);

        ArgumentCaptor<PatchEntity> captor = ArgumentCaptor.forClass(PatchEntity.class);
        verify(patchRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(patch.getId());
        assertThat(saved.getTitle()).isEqualTo(patch.getTitle());
        assertThat(saved.getStatus()).isEqualTo(PatchStatus.OPEN);
    }

    @Test
    void saveMembershipPersisteEntidadActiva() {
        PatchMembership membership = PatchMembership.builder()
                .id(UUID.randomUUID())
                .patchId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(MembershipStatus.LEFT)
                .build();
        PatchWriteRepositoryAdapter adapter = new PatchWriteRepositoryAdapter(patchRepository, membershipRepository, mapper);

        PatchMembership saved = adapter.saveMembership(membership);

        ArgumentCaptor<PatchMembershipEntity> captor = ArgumentCaptor.forClass(PatchMembershipEntity.class);
        verify(membershipRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(membership.getId());
        assertThat(captor.getValue().getPatchId()).isEqualTo(membership.getPatchId());
        assertThat(captor.getValue().getUserId()).isEqualTo(membership.getUserId());
        assertThat(captor.getValue().getStatus()).isEqualTo(MembershipStatus.ACTIVE);
        assertThat(captor.getValue().getJoinedAt()).isNotNull();
        assertThat(saved).isSameAs(membership);
    }

    private Patch patch() {
        return Patch.builder()
                .id(UUID.randomUUID())
                .title("Patch")
                .description("Descripcion")
                .category(PatchCategory.STUDY)
                .location("Salon 1")
                .campusZone(CampusZone.SALON)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .capacity(5)
                .currentCount(1)
                .status(PatchStatus.OPEN)
                .creatorId(UUID.randomUUID())
                .isPublic(true)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
