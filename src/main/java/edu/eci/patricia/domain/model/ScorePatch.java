package edu.eci.patricia.domain.model;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ScorePatch {

    private UUID patchId;
    private float totalScore;

    private float tagScore;
    private float historyScore;
    private float connectionScore;

    private String reason;

}
