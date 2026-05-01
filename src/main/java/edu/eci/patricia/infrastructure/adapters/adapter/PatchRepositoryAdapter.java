package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PatchRepositoryAdapter implements PatchRepositoryPort {

    private final PatchJpaRepository jpaRepository;
    private final PatchEntityMapper  mapper;

    public PatchRepositoryAdapter(PatchJpaRepository jpaRepository, PatchEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    @Override
    public List<Patch> findOpenPublicPatches() {
        return jpaRepository.findByStatusAndIsPublic(PatchStatus.OPEN, true)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Patch> findByIds(List<UUID> ids) {
        return jpaRepository.findAllById(ids)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Patch> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
