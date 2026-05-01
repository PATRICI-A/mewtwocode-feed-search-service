package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface PatchRepositoryPort {
    List<Patch> findOpenPublicPatches();
    List<Patch> findByIds(List<UUID> ids);
    Optional<Patch> findById(UUID id);
}
