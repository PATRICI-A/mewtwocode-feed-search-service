package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.PatchCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCategoryScore {
    private UUID id;
    private UUID userId;
    private PatchCategory category;
    private float scoreTotal;
    private LocalDateTime lastUpdated;
}
