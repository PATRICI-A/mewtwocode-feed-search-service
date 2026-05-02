package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.enums.PatchStatus;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.PatchSpecification;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PatchRepositoryAdapter implements PatchRepositoryPort {

    private final PatchJpaRepository jpaRepository;
    private final PatchEntityMapper mapper;

    public PatchRepositoryAdapter(PatchJpaRepository jpaRepository, PatchEntityMapper mapper) {
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

    @Override
    public List<Patch> searchPatches(SearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.findAll(PatchSpecification.fromRequest(request), pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countPatches(SearchRequest request) {
        return jpaRepository.count(PatchSpecification.fromRequest(request));
    }

    @Override
    public List<Patch> findPopularPatches(int limit) {
        return jpaRepository.findTop10ByStatusAndIsPublicOrderByCurrentCountDesc(PatchStatus.OPEN, true)
                .stream()
                .limit(limit)
                .map(mapper::toDomain)
                .toList();
    }
}
