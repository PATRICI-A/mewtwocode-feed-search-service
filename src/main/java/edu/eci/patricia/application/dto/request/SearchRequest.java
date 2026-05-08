package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.CampusZone;
import edu.eci.patricia.domain.model.enums.PatchCategory;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    @Size(min = 2, message = "El término de búsqueda debe tener al menos 2 caracteres")
    @Schema(description = "Texto a buscar en título y descripción (mínimo 2 caracteres)", example = "fútbol")
    private String q;

    @Schema(description = "Categoría del parche", example = "SPORTS")
    private PatchCategory category;
    @Schema(description = "Zona del campus", example = "CAFETERIA")
    private CampusZone campusZone;

    @Schema(description = "Estado del parche", example = "OPEN")
    private PatchStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Fecha inicio del rango (inclusive)", example = "2025-06-01")
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Fecha fin del rango (inclusive)", example = "2025-06-30")
    private LocalDate dateTo;

    @Schema(description = "Capacidad máxima del grupo", example = "10")
    private Integer maxGroupSize;

    @Schema(description = "Solo parches con cupos disponibles", example = "true")
    private Boolean hasAvailableSpots;
}
