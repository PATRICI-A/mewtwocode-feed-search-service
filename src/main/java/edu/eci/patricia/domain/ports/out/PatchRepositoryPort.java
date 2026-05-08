package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface PatchRepositoryPort {
    List<Patch> findOpenPublicPatches();
    List<Patch> findByIds(List<UUID> ids);
    List<Patch> searchPatches(edu.eci.patricia.application.dto.request.SearchRequest request, int page, int size);
    long countPatches(edu.eci.patricia.application.dto.request.SearchRequest request);
    List<Patch> findPopularPatches(int limit);
    Optional<Patch> findById(UUID id);
}
