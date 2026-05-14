package edu.eci.patricia.domain.ports.in;

import java.util.UUID;

/**
 * Inbound port (use-case boundary) that handles the business logic for a user
 * joining a patch. Validates that the patch exists, is open, and has available
 * capacity before creating the membership record.
 */
public interface JoinPatchUseCase {

    /**
     * Executes the join operation for the specified user and patch.
     *
     * @param patchId the unique identifier of the patch to join
     * @param userId  the unique identifier of the user who wants to join
     * @throws edu.eci.patricia.domain.exceptions.PatchNotFoundException if the patch does not exist
     * @throws edu.eci.patricia.domain.exceptions.PatchFullException      if the patch has reached its capacity
     * @throws edu.eci.patricia.domain.exceptions.AlreadyMemberException  if the user is already an active member
     */
    void execute(UUID patchId, UUID userId);
}