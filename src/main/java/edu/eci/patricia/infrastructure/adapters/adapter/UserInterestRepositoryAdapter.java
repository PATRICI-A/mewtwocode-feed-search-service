package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.UserInterestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserInterestRepositoryAdapter implements UserInterestRepositoryPort {

    private final UserInterestJpaRepository jpaRepository;
    private final PatchEntityMapper mapper;

    public UserInterestRepositoryAdapter(UserInterestJpaRepository jpaRepository, PatchEntityMapper mapper) {
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
