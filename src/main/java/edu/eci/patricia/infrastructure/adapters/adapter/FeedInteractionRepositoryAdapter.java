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

/**
 * Infrastructure adapter that bridges the {@link FeedInteractionRepositoryPort} domain port
 * and the JPA-backed {@link FeedInteractionJpaRepository}.
 *
 * <p>Handles persistence of user interactions (view, join, skip) with patches
 * and translates entities to domain objects via {@link PatchEntityMapper}.</p>
 */
@Component
public class FeedInteractionRepositoryAdapter implements FeedInteractionRepositoryPort {

    private final FeedInteractionJpaRepository jpaRepository;
    private final PatchEntityMapper mapper;

    /**
     * Constructs the adapter with its required dependencies.
     *
     * @param jpaRepository the JPA repository for {@code feed_interactions} table
     * @param mapper        the mapper used to convert entities to domain objects
     */
    public FeedInteractionRepositoryAdapter(FeedInteractionJpaRepository jpaRepository, PatchEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves all recorded interactions for a given user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link FeedInteraction} domain objects; never {@code null}, may be empty
     */
    @Override
    public List<FeedInteraction> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Returns the set of patch IDs that the user has joined.
     *
     * @param userId the unique identifier of the user
     * @return a {@link Set} of patch UUIDs for which a JOIN interaction was recorded
     */
    @Override
    public Set<UUID> findJoinedPatchIds(UUID userId) {
        return jpaRepository.findJoinedPatchIdsByUserId(userId).stream()
                .collect(Collectors.toSet());
    }

    /**
     * Persists a new interaction record for a user and a patch.
     *
     * @param userId  the unique identifier of the user performing the interaction
     * @param patchId the unique identifier of the patch being interacted with
     * @param type    the type of interaction (e.g. VIEW, JOIN, SKIP)
     */
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
