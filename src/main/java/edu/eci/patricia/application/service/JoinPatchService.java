package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.exceptions.AlreadyMemberException;
import edu.eci.patricia.domain.exceptions.BusinessRuleException;
import edu.eci.patricia.domain.exceptions.PatchFullException;
import edu.eci.patricia.domain.exceptions.PatchNotFoundException;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;
import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.in.JoinPatchUseCase;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchWriteRepositoryPort;
import edu.eci.patricia.infrastructure.external.NotificationFeignClient;
import edu.eci.patricia.infrastructure.external.NotificationRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JoinPatchService implements JoinPatchUseCase {

    private final PatchRepositoryPort patchRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchWriteRepositoryPort patchWriteRepository;
    private final NotificationFeignClient notificationFeignClient;

    public JoinPatchService(
            PatchRepositoryPort patchRepository,
            MembershipRepositoryPort membershipRepository,
            PatchWriteRepositoryPort patchWriteRepository,
            NotificationFeignClient notificationFeignClient
    ) {
        this.patchRepository = patchRepository;
        this.membershipRepository = membershipRepository;
        this.patchWriteRepository = patchWriteRepository;
        this.notificationFeignClient = notificationFeignClient;
    }

    @Override
    public void execute(UUID patchId, UUID userId) {

        Patch patch = patchRepository.findById(patchId)
                .orElseThrow(() -> new PatchNotFoundException(patchId));

        if (!patch.getIsPublic()) {
            throw new BusinessRuleException("Cannot join a private patch.");
        }

        if (patch.getStatus() != PatchStatus.OPEN) {
            throw new BusinessRuleException(
                    "Patch is not open. Current status: " + patch.getStatus()
            );
        }

        if (patch.getCurrentCount() >= patch.getCapacity()) {
            throw new PatchFullException(patchId);
        }

        if (membershipRepository.existsActiveMembership(patchId, userId)) {
            throw new AlreadyMemberException(patchId, userId);
        }

        PatchMembership membership = PatchMembership.builder()
                .id(UUID.randomUUID())
                .patchId(patchId)
                .userId(userId)
                .status(MembershipStatus.ACTIVE)
                .build();

        patchWriteRepository.saveMembership(membership);

        patch.setCurrentCount(patch.getCurrentCount() + 1);

        if (patch.getCurrentCount() >= patch.getCapacity()) {
            patch.setStatus(PatchStatus.FULL);
        }

        patchWriteRepository.save(patch);

        notificationFeignClient.sendNotification(
                new NotificationRequest(
                        patch.getCreatorId(),
                        "PATCH_JOIN",
                        "Nuevo miembro",
                        "Un usuario se unió a tu parche",
                        patch.getId().toString()
                )
        );
    }
}