package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.exceptions.AlreadyMemberException;
import edu.eci.patricia.domain.exceptions.BusinessRuleException;
import edu.eci.patricia.domain.exceptions.PatchFullException;
import edu.eci.patricia.domain.exceptions.PatchNotFoundException;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchWriteRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinPatchServiceTest {

    @Mock private PatchRepositoryPort patchRepository;
    @Mock private MembershipRepositoryPort membershipRepository;
    @Mock private PatchWriteRepositoryPort patchWriteRepository;

    private JoinPatchService service;

    private final UUID patchId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new JoinPatchService(patchRepository, membershipRepository, patchWriteRepository);
    }

    @Test
    void executeCreaMembresiaEIncrementaCupo() {
        Patch patch = openPatch(true, 1, 3);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(patch));
        when(membershipRepository.existsActiveMembership(patchId, userId)).thenReturn(false);

        service.execute(patchId, userId);

        ArgumentCaptor<PatchMembership> membershipCaptor = ArgumentCaptor.forClass(PatchMembership.class);
        verify(patchWriteRepository).saveMembership(membershipCaptor.capture());
        assertThat(membershipCaptor.getValue().getPatchId()).isEqualTo(patchId);
        assertThat(membershipCaptor.getValue().getUserId()).isEqualTo(userId);
        assertThat(patch.getCurrentCount()).isEqualTo(2);
        assertThat(patch.getStatus()).isEqualTo(PatchStatus.OPEN);
        verify(patchWriteRepository).save(patch);
    }

    @Test
    void executeMarcaFullCuandoLlegaALaCapacidad() {
        Patch patch = openPatch(true, 2, 3);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(patch));
        when(membershipRepository.existsActiveMembership(patchId, userId)).thenReturn(false);

        service.execute(patchId, userId);

        assertThat(patch.getCurrentCount()).isEqualTo(3);
        assertThat(patch.getStatus()).isEqualTo(PatchStatus.FULL);
        verify(patchWriteRepository).save(patch);
    }

    @Test
    void executeFallaSiPatchNoExiste() {
        when(patchRepository.findById(patchId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(patchId, userId))
                .isInstanceOf(PatchNotFoundException.class);
    }

    @Test
    void executeFallaSiPatchPrivadoCerradoLlenoOMiembroExistente() {
        Patch privatePatch = openPatch(false, 0, 3);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(privatePatch));
        assertThatThrownBy(() -> service.execute(patchId, userId)).isInstanceOf(BusinessRuleException.class);

        Patch closedPatch = openPatch(true, 0, 3);
        closedPatch.setStatus(PatchStatus.CLOSED);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(closedPatch));
        assertThatThrownBy(() -> service.execute(patchId, userId)).isInstanceOf(BusinessRuleException.class);

        Patch fullPatch = openPatch(true, 3, 3);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(fullPatch));
        assertThatThrownBy(() -> service.execute(patchId, userId)).isInstanceOf(PatchFullException.class);

        Patch alreadyMemberPatch = openPatch(true, 1, 3);
        when(patchRepository.findById(patchId)).thenReturn(Optional.of(alreadyMemberPatch));
        when(membershipRepository.existsActiveMembership(patchId, userId)).thenReturn(true);
        assertThatThrownBy(() -> service.execute(patchId, userId)).isInstanceOf(AlreadyMemberException.class);

        verify(patchWriteRepository, never()).saveMembership(org.mockito.ArgumentMatchers.any());
    }

    private Patch openPatch(boolean isPublic, int currentCount, int capacity) {
        return Patch.builder()
                .id(patchId)
                .isPublic(isPublic)
                .status(PatchStatus.OPEN)
                .currentCount(currentCount)
                .capacity(capacity)
                .build();
    }
}
