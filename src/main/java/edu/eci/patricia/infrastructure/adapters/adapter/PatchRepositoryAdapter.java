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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter that bridges the {@link PatchRepositoryPort} read-only domain port
 * and the JPA-backed {@link PatchJpaRepository}.
 *
 * <p>Supports feed retrieval, dynamic search with specifications, popular patch ranking,
 * and single-patch lookup. All results are mapped to domain objects via {@link PatchEntityMapper}.</p>
 */
@Component
public class PatchRepositoryAdapter implements PatchRepositoryPort {

    private final PatchJpaRepository jpaRepository;
    private final PatchEntityMapper  mapper;

    /**
     * Constructs the adapter with its required dependencies.
     *
     * @param jpaRepository the JPA repository for {@code patches} table
     * @param mapper        the mapper used to convert entities to domain objects
     */
    public PatchRepositoryAdapter(PatchJpaRepository jpaRepository, PatchEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper        = mapper;
    }

    /**
     * Returns all patches that are currently open and visible to the public.
     *
     * @return a list of open, public {@link Patch} domain objects; never {@code null}, may be empty
     */
    @Override
    public List<Patch> findOpenPublicPatches() {
        return jpaRepository.findByStatusAndIsPublic(PatchStatus.OPEN, true)
                .stream().map(mapper::toDomain).toList();
    }

    /**
     * Fetches multiple patches by their IDs in a single query.
     *
     * @param ids the list of patch UUIDs to retrieve
     * @return a list of {@link Patch} domain objects matching the supplied IDs
     */
    @Override
    public List<Patch> findByIds(List<UUID> ids) {
        return jpaRepository.findAllById(ids)
                .stream().map(mapper::toDomain).toList();
    }

    /**
     * Retrieves a single patch by its unique identifier.
     *
     * @param id the unique identifier of the patch
     * @return an {@link Optional} containing the {@link Patch} if found, or empty if not found
     */
    @Override
    public Optional<Patch> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * Executes a dynamic filtered search using the criteria in the supplied request object,
     * returning a paginated slice sorted by start time ascending.
     *
     * @param request the search filters (keyword, category, campus zone, dates, capacity, etc.)
     * @param page    the zero-based page number
     * @param size    the maximum number of results per page
     * @return a list of {@link Patch} domain objects matching all active filters on the requested page
     */
    @Override
    public List<Patch> searchPatches(SearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));
        return jpaRepository.findAll(PatchSpecification.fromRequest(request), pageable)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Counts the total number of public patches that match the given search filters.
     *
     * @param request the search filters to apply
     * @return the total count of matching patches
     */
    @Override
    public long countPatches(SearchRequest request) {
        return jpaRepository.count(PatchSpecification.fromRequest(request));
    }

    /**
     * Returns the most popular open public patches, ranked by current participant count descending,
     * up to the requested limit.
     *
     * @param limit the maximum number of patches to return
     * @return a list of popular {@link Patch} domain objects, at most {@code limit} entries
     */
    @Override
    public List<Patch> findPopularPatches(int limit) {
        return jpaRepository.findTop10ByStatusAndIsPublicOrderByCurrentCountDesc(PatchStatus.OPEN, true)
                .stream()
                .limit(limit)
                .map(mapper::toDomain)
                .toList();
    }
}
