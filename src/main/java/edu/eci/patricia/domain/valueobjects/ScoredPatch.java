package edu.eci.patricia.domain.valueobjects;

import edu.eci.patricia.domain.model.Patch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ScoredPatch {
    private final Patch patch;
    private final Float affinityScore;
    private final String reason;

    public UUID getPatchId() {
        return patch.getId();
    }
}
