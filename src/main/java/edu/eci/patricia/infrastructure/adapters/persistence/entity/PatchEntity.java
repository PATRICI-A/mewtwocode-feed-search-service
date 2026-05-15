package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a patch (study/social group session) stored in the {@code patches} table.
 *
 * <p>A patch holds all scheduling, capacity, visibility, and categorisation data for a
 * user-created event on campus. Default values for {@code currentCount}, {@code status},
 * and {@code isPublic} are initialised by the {@link #onCreate()} lifecycle callback.</p>
 */
@Entity
@Table(name = "patches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatchCategory category;

    @Column(length = 250)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "campus_zone", nullable = false)
    private CampusZone campusZone;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_count", nullable = false)
    private Integer currentCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatchStatus status = PatchStatus.OPEN;

    @Column(name = "creator_id", nullable = false, updatable = false)
    private UUID creatorId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback invoked before the entity is first persisted.
     *
     * <p>Sets {@code createdAt} to the current time and applies default values for
     * {@code currentCount}, {@code status}, and {@code isPublic} if they have not been set.</p>
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.currentCount == null) this.currentCount = 0;
        if (this.status == null) this.status = PatchStatus.OPEN;
        if (this.isPublic == null) this.isPublic = true;
    }
}
