package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a single interest tag registered by a user, stored in the {@code user_interests} table.
 *
 * <p>A unique constraint on {@code (user_id, interest_tag)} ensures that the same tag cannot be
 * registered more than once per user. Interest tags are used by the feed ranking algorithm to boost
 * patches whose category or keywords match the user's declared interests.</p>
 */
@Entity
@Table(name = "user_interests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_interest_user_tag", columnNames = {"user_id", "interest_tag"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "interest_tag", nullable = false, length = 100)
    private String interestTag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
