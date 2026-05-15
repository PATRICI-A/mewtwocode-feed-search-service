package edu.eci.patricia.application.mapper;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.domain.model.Patch;
import org.springframework.stereotype.Component;

/**
 * Spring component responsible for converting {@link Patch} domain objects into
 * {@link PatchSummaryResponse} DTOs used by the API layer.
 * Keeps the domain model decoupled from the presentation layer.
 */
@Component
public class PatchDomainMapper {

    /**
     * Maps a {@link Patch} domain object to a {@link PatchSummaryResponse} DTO,
     * enriching it with membership status and affinity score that are not part of the core domain.
     *
     * @param patch          the domain patch to convert; must not be {@code null}
     * @param userIsMember   {@code true} if the requesting user is an active member of the patch
     * @param affinityScore  personalised relevance score in the range [0.0, 1.0], or {@code null}
     *                       when affinity is not applicable (e.g. plain search results)
     * @return a fully populated {@link PatchSummaryResponse}
     */
    public PatchSummaryResponse toResponse(Patch patch, boolean userIsMember, Float affinityScore) {
        return PatchSummaryResponse.builder()
                .id(patch.getId())
                .title(patch.getTitle())
                .description(patch.getDescription())
                .category(patch.getCategory())
                .campusZone(patch.getCampusZone())
                .startTime(patch.getStartTime())
                .capacity(patch.getCapacity())
                .currentCount(patch.getCurrentCount())
                .status(patch.getStatus())
                .isPublic(patch.getIsPublic())
                .creatorName("Unknown")
                .affinityScore(affinityScore)
                .userIsMember(userIsMember)
                .build();
    }
}
