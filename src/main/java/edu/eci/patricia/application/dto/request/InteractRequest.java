package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.InteractionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Data Transfer Object that carries the type of interaction a user performed on a patch.
 * The action determines the category-score delta applied by {@code RegisterInteractionService}:
 * VIEW (+0.1), JOIN (+1.0), or SKIP (-10.0).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for a user interaction with a patch")
public class InteractRequest {

    @NotNull(message = "Action is required")
    @Schema(
        description = "Action performed on the patch. " +
                      "VIEW: user viewed the patch (+0.1 category score). " +
                      "JOIN: user joined (+1.0 category score, excludes patch from future recommendations). " +
                      "SKIP: user dismissed the patch (-10.0 category score).",
        example = "JOIN",
        allowableValues = {"VIEW", "JOIN", "SKIP"}
    )
    private InteractionType action;
}
