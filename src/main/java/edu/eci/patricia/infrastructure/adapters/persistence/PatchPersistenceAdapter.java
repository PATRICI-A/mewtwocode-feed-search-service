package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchPersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PatchPersistenceAdapter implements PatchRepositoryPort {

    private final PatchJpaRepository jpaRepository;
    private final PatchPersistenceMapper mapper;

    public PatchPersistenceAdapter(PatchJpaRepository jpaRepository, PatchPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Patch> findOpenPublicPatches() {
        return jpaRepository.findByStatusAndIsPublic(PatchStatus.OPEN, true).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Patch> findByIds(List<UUID> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
