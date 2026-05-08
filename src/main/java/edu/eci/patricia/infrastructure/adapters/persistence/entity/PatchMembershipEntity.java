package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patch_memberships", uniqueConstraints = {
        @UniqueConstraint(name = "uk_membership_patch_user", columnNames = {"patch_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchMembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "patch_id", nullable = false, updatable = false)
    private UUID patchId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status = MembershipStatus.ACTIVE;
}
