package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing the accumulated affinity score of a user for a patch category,
 * stored in the {@code user_category_scores} table.
 *
 * <p>A unique constraint on {@code (user_id, category)} guarantees at most one score record per
 * user per category. Scores are updated by the interaction registration use case using a
 * time-decay formula and are clamped between 0 and 100.</p>
 */
@Entity
@Table(name = "user_category_scores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_category", columnNames = {"user_id", "category"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCategoryScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PatchCategory category;

    @Column(name = "score_total", nullable = false)
    private float scoreTotal;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
