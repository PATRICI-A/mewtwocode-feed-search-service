package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FeedInteraction{
    private UUID id;
    private UUID userId;
    private UUID patchId;
    private LocalDateTime interactedAt;
    private InteractionType action;
}