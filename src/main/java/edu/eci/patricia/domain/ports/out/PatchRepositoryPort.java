package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;
import java.util.List;
import java.util.UUID;

public interface PatchRepositoryPort {
    List<Patch> findOpenPublicPatches();
    List<Patch> findByIds(List<UUID> ids);
}
