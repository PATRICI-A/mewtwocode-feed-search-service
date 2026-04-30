package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.valueobjects.ScoredPatch;

import java.util.List;
import java.util.UUID;

public interface GetRecommendationsPort {
    List<ScoredPatch> getRecommendations(UUID userId);
}
