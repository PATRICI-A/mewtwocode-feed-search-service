package edu.eci.patricia.application.dto.response;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight Data Transfer Object that summarises the key attributes of a patch for list and feed views.
 * Used by the feed, search, and recommendation endpoints to avoid exposing the full domain model.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchSummaryResponse {
    /** Unique identifier of the patch. */
    private UUID id;
    /** Display title of the patch. */
    private String title;
    /** Short description of the patch activity. */
    private String description;
    /** Category that classifies the patch (e.g. SPORTS, STUDY). */
    private PatchCategory category;
    /** Campus zone where the patch takes place. */
    private CampusZone campusZone;
    /** Scheduled start date and time of the patch. */
    private LocalDateTime startTime;
    /** Maximum number of participants allowed. */
    private Integer capacity;
    /** Current number of active members. */
    private Integer currentCount;
    /** Current lifecycle status of the patch (OPEN, FULL, CLOSED, etc.). */
    private PatchStatus status;
    /** Whether the patch is visible to all users ({@code true}) or invitation-only ({@code false}). */
    private Boolean isPublic;
    /** Display name of the user who created the patch. */
    private String creatorName;
    /** Personalised affinity score in the range [0.0, 1.0]; {@code null} when not applicable. */
    private Float affinityScore;
    /** Whether the requesting user is an active member of this patch. */
    private Boolean userIsMember;
}
