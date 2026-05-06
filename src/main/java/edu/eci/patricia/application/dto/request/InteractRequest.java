package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.InteractionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cuerpo de la interacción del usuario con un parche")
public class InteractRequest {

    @NotNull(message = "Defina al menos una acción")
    @Schema(
        description = "Tipo de acción realizada sobre el parche. " +
                      "VIEW: el usuario vio el parche (+0.1 en score de categoría). " +
                      "JOIN: el usuario se unió (+1.0 en score, excluye el parche de futuras recomendaciones). " +
                      "SKIP: el usuario ignoró el parche (-10.0 en score de categoría).",
        example = "JOIN",
        allowableValues = {"VIEW", "JOIN", "SKIP"}
    )
    private InteractionType action;
}
