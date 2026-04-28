package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PatchMembership{
    private UUID id;
    private UUID patchId;
    private UUID userId;
    private LocalDateTime joinedAt;
    private MembershipStatus status;
}