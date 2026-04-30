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
public class PatchMembership {
    private UUID id;
    private UUID patchId;
    private UUID userId;
    private LocalDateTime joinedAt;
    private MembershipStatus status;
}
