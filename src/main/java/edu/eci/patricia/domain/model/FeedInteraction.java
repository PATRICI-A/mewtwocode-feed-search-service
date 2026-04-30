package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.InteractionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedInteraction {
    private UUID id;
    private UUID userId;
    private UUID patchId;
    private LocalDateTime interactedAt;
    private InteractionType action;
}
