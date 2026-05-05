package edu.eci.patricia.entrypoints.rest.controller;

import edu.eci.patricia.domain.ports.in.JoinPatchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "Feed personalizado de parches")
public class JoinPatchController {

    private final JoinPatchUseCase joinPatchUseCase;

    public JoinPatchController(JoinPatchUseCase joinPatchUseCase) {
        this.joinPatchUseCase = joinPatchUseCase;
    }

    @Operation(
            summary = "Unirse a un parche desde el feed ",
            description = "Pone al usuario a un parche público con cupo disponible"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario unido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Parche no encontrado"),
            @ApiResponse(responseCode = "409", description = "El usuario ya es miembro"),
            @ApiResponse(responseCode = "422", description = "Parche lleno o cerrado"),
            @ApiResponse(responseCode = "401", description = "JWT inválido o ausente")
    })
    @PostMapping("/{patchId}/join")
    public ResponseEntity<Void> joinPatch(
            @PathVariable UUID patchId,
            @RequestParam UUID userId) {

        joinPatchUseCase.execute(patchId, userId);
        return ResponseEntity.ok().build();
    }
}