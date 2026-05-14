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
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application service that handles the "join patch" use case.
 *
 * <p>Before creating a membership the service enforces the following business rules in order:
 * <ol>
 *   <li>The patch must exist.</li>
 *   <li>The patch must be public.</li>
 *   <li>The patch status must be {@code OPEN}.</li>
 *   <li>The patch must not have reached its capacity.</li>
 *   <li>The user must not already be an active member.</li>
 * </ol>
 *
 * <p>Upon successful validation a new {@link PatchMembership} with status {@code ACTIVE} is
 * persisted and the patch's {@code currentCount} is incremented. If the count reaches the
 * capacity the patch status is automatically updated to {@code FULL}.
 */
@Service
public class JoinPatchService implements JoinPatchUseCase {

    private final PatchRepositoryPort      patchRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchWriteRepositoryPort patchWriteRepository;

    /**
     * Constructs a {@code JoinPatchService} with all required collaborators.
     *
     * @param patchRepository      port for reading patch data
     * @param membershipRepository port for checking existing active memberships
     * @param patchWriteRepository port for persisting new memberships and updating patch state
     */
    public JoinPatchService(PatchRepositoryPort patchRepository,
                            MembershipRepositoryPort membershipRepository,
                            PatchWriteRepositoryPort patchWriteRepository) {
        this.patchRepository      = patchRepository;
        this.membershipRepository = membershipRepository;
        this.patchWriteRepository = patchWriteRepository;
    }

    /**
     * Validates all business rules and joins the specified user to the patch.
     *
     * @param patchId the identifier of the patch to join
     * @param userId  the identifier of the user who wants to join
     * @throws PatchNotFoundException  if no patch with the given {@code patchId} exists
     * @throws BusinessRuleException   if the patch is private or not in {@code OPEN} status
     * @throws PatchFullException      if the patch has already reached its maximum capacity
     * @throws AlreadyMemberException  if the user is already an active member of the patch
     */
    @Override
    public void execute(UUID patchId, UUID userId) {


        Patch patch = patchRepository.findById(patchId)
                .orElseThrow(() -> new PatchNotFoundException(patchId));


        if (!patch.getIsPublic())
            throw new BusinessRuleException("Cannot join a private patch.");

        if (patch.getStatus() != PatchStatus.OPEN)
            throw new BusinessRuleException("Patch is not open. Current status: " + patch.getStatus());


        if (patch.getCurrentCount() >= patch.getCapacity())
            throw new PatchFullException(patchId);


        if (membershipRepository.existsActiveMembership(patchId, userId))
            throw new AlreadyMemberException(patchId, userId);


        PatchMembership membership = PatchMembership.builder()
                .id(UUID.randomUUID())
                .patchId(patchId)
                .userId(userId)
                .status(MembershipStatus.ACTIVE)
                .build();
        patchWriteRepository.saveMembership(membership);


        patch.setCurrentCount(patch.getCurrentCount() + 1);
        if (patch.getCurrentCount() >= patch.getCapacity())
            patch.setStatus(PatchStatus.FULL);
        patchWriteRepository.save(patch);
    }
}