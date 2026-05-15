package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Domain model representing the membership relationship between a user and a patch.
 * Tracks when the user joined and the current state of the membership, allowing
 * the system to distinguish active participants from those who left or were removed.
 */
public class PatchMembership {
    /** Unique identifier for this membership record. */
    private UUID id;

    /** Identifier of the patch this membership belongs to. */
    private UUID patchId;

    /** Identifier of the user who holds this membership. */
    private UUID userId;

    /** Timestamp at which the user joined the patch. */
    private LocalDateTime joinedAt;

    /** Current status of this membership. */
    private MembershipStatus status;
}
