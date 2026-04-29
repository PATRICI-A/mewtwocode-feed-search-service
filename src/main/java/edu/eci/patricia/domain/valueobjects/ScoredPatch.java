package edu.eci.patricia.domain.valueobjects;


import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor

public class ScoredPatch{
    private final UUID patchId;
    private final Float affinityScore;
    private final String reason;
}