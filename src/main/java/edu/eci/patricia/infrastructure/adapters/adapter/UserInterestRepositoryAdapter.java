package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserInterestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Infrastructure adapter that bridges the {@link UserInterestRepositoryPort} domain port
 * and the JPA-backed {@link UserInterestJpaRepository}.
 *
 * <p>Translates persistence entities to domain model objects using {@link PatchEntityMapper}.</p>
 */
@Component
public class UserInterestRepositoryAdapter implements UserInterestRepositoryPort {

    private final UserInterestJpaRepository jpaRepository;
    private final PatchEntityMapper mapper;

    /**
     * Constructs the adapter with its required dependencies.
     *
     * @param jpaRepository the JPA repository for {@code user_interests} table
     * @param mapper        the mapper used to convert entities to domain objects
     */
    public UserInterestRepositoryAdapter(UserInterestJpaRepository jpaRepository, PatchEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Retrieves all interest tags registered for the given user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link UserInterest} domain objects; never {@code null}, may be empty
     */
    @Override
    public List<UserInterest> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
