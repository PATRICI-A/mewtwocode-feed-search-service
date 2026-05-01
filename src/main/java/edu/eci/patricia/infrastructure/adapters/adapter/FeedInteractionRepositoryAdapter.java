package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.FeedInteraction;
import edu.eci.patricia.domain.model.enums.InteractionType;
import edu.eci.patricia.domain.ports.out.FeedInteractionRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.FeedInteractionEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.FeedInteractionJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FeedInteractionRepositoryAdapter implements FeedInteractionRepositoryPort {

    private final FeedInteractionJpaRepository jpaRepository;
    private final PatchEntityMapper mapper;

    public FeedInteractionRepositoryAdapter(FeedInteractionJpaRepository jpaRepository, PatchEntityMapper mapper) {
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
    public Set<UUID> findJoinedPatchIds(UUID userId) {
        return jpaRepository.findJoinedPatchIdsByUserId(userId).stream()
                .collect(Collectors.toSet());
    }

    @Override
    public void save(UUID userId, UUID patchId, InteractionType type) {
        FeedInteractionEntity entity = FeedInteractionEntity.builder()
                .userId(userId)
                .patchId(patchId)
                .action(type)
                .interactedAt(LocalDateTime.now())
                .build();
        jpaRepository.save(entity);
    }
}
