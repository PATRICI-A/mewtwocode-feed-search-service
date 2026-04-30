package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.enums.InteractionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feed_interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedInteractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "patch_id", nullable = false, updatable = false)
    private UUID patchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private InteractionType action;

    @Column(name = "interacted_at", nullable = false, updatable = false)
    private LocalDateTime interactedAt;
}
