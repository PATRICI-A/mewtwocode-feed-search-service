package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recommendation_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RecommendationCacheEntity {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "recommended_patch_ids", columnDefinition = "TEXT")
    private String recommendedPatchIds;

    @Column(name = "score_breakdown", columnDefinition = "TEXT")
    private String scoreBreakdown;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}