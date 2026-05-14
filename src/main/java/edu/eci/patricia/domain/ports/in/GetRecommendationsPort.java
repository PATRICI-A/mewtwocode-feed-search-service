package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.valueobjects.ScoredPatch;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port (use-case boundary) for retrieving personalised patch recommendations
 * for a given user. Implementations apply the recommendation algorithm that combines
 * category affinity scores and user interaction history to produce a ranked list.
 */
public interface GetRecommendationsPort {

    /**
     * Returns a ranked list of patch recommendations tailored to the specified user.
     *
     * @param userId the unique identifier of the user requesting recommendations
     * @return an ordered list of {@link ScoredPatch} objects, each carrying the patch
     *         and its computed affinity score; never {@code null}, may be empty
     */
    List<ScoredPatch> getRecommendations(UUID userId);
}
