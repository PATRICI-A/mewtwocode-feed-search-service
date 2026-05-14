package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Core domain model representing a patch — a time-bounded, location-aware
 * group activity created by a campus user. A patch has a category, a capacity
 * limit, a visibility setting, and a lifecycle status that governs whether
 * new members can join.
 */
public class Patch {
    /** Unique identifier for this patch. */
    private UUID id;

    /** Short descriptive title of the patch activity. */
    private String title;

    /** Detailed description of what the patch is about. */
    private String description;

    /** Thematic category that classifies this patch. */
    private PatchCategory category;

    /** Free-text location detail (e.g. room number, meeting point). */
    private String location;

    /** Predefined campus zone where this patch takes place. */
    private CampusZone campusZone;

    /** Date and time when the patch activity begins. */
    private LocalDateTime startTime;

    /** Date and time when the patch activity ends. */
    private LocalDateTime endTime;

    /** Maximum number of members allowed in this patch. */
    private Integer capacity;

    /** Current number of active members in this patch. */
    private Integer currentCount;

    /** Current lifecycle status of this patch. */
    private PatchStatus status;

    /** Identifier of the user who created this patch. */
    private UUID creatorId;

    /** Whether this patch is visible to all users ({@code true}) or invite-only ({@code false}). */
    private Boolean isPublic;

    /** Timestamp at which this patch was created. */
    private LocalDateTime createdTime;

    /**
     * Determines whether this patch has reached its maximum capacity.
     *
     * @return {@code true} if {@code currentCount} is greater than or equal to {@code capacity};
     *         {@code false} otherwise
     */
    public boolean isFull() {
        return this.currentCount >= this.capacity;
    }

    /**
     * Determines whether this patch is currently open for new members.
     *
     * @return {@code true} if the patch status is {@link PatchStatus#OPEN};
     *         {@code false} otherwise
     */
    public boolean isOpen() {
        return this.status == PatchStatus.OPEN;
    }
}
