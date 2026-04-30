package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchPersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.FeedInteractionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FeedInteractionPersistenceAdapter implements FeedInteractionRepositoryPort {

    private final FeedInteractionJpaRepository jpaRepository;
    private final PatchPersistenceMapper mapper;

    public FeedInteractionPersistenceAdapter(FeedInteractionJpaRepository jpaRepository, PatchPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<FeedInteraction> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Set<UUID> findInteractedPatchIds(UUID userId) {
        return jpaRepository.findDistinctPatchIdsByUserId(userId).stream()
                .collect(Collectors.toSet());
    }
}
