package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InteractRequest{
    @NotNull(message = "Defina al menos una acción")
    private InteractionType action;
}
