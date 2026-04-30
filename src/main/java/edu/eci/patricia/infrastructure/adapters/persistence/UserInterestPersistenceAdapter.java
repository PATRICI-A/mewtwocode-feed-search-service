package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchPersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserInterestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserInterestPersistenceAdapter implements UserInterestRepositoryPort {

    private final UserInterestJpaRepository jpaRepository;
    private final PatchPersistenceMapper mapper;

    public UserInterestPersistenceAdapter(UserInterestJpaRepository jpaRepository, PatchPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserInterest> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
