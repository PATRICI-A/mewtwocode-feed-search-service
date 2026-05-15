package edu.eci.patricia.application.usecase;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.dto.response.SearchResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.ports.in.SearchPatchesUseCase;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the {@link SearchPatchesUseCase} that searches patches using a
 * JPA Specification pattern with up to eight combinable predicates.
 *
 * <p>Supported predicates (all optional):
 * <ol>
 *   <li>Free-text query ({@code q}) matched against title and description.</li>
 *   <li>Category filter.</li>
 *   <li>Campus zone filter.</li>
 *   <li>Patch status filter.</li>
 *   <li>Date range start ({@code dateFrom}, inclusive).</li>
 *   <li>Date range end ({@code dateTo}, inclusive).</li>
 *   <li>Maximum group size.</li>
 *   <li>Has available spots (current count &lt; capacity).</li>
 * </ol>
 *
 * <p>Results are paginated and each patch is enriched with the requesting user's membership
 * status. When no user is authenticated membership defaults to {@code false} for all results.
 */
@Service
public class SearchPatchesUseCaseImpl implements SearchPatchesUseCase {

    private final PatchRepositoryPort patchRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchDomainMapper mapper;

    /**
     * Constructs a {@code SearchPatchesUseCaseImpl} with all required collaborators.
     *
     * @param patchRepository      port for counting and querying patches with search criteria
     * @param membershipRepository port for determining which patches the user is a member of
     * @param mapper               mapper that converts domain patches to response DTOs
     */
    public SearchPatchesUseCaseImpl(PatchRepositoryPort patchRepository,
                                   MembershipRepositoryPort membershipRepository,
                                   PatchDomainMapper mapper) {
        this.patchRepository = patchRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
    }

    /**
     * Executes the patch search use case: counts total matches, fetches the requested page,
     * resolves membership for each result, and assembles a paginated {@link SearchResponse}.
     *
     * @param userId  the identifier of the requesting user, or {@code null} for unauthenticated
     *                requests (membership will be {@code false} for all results)
     * @param request the search criteria; all fields are optional and combinable
     * @param page    zero-based page index
     * @param size    maximum number of results to return per page
     * @return a {@link SearchResponse} containing the paginated results and pagination metadata
     */
    @Override
    public SearchResponse execute(UUID userId, SearchRequest request, int page, int size) {
        long total = patchRepository.countPatches(request);
        int totalPages = (int) Math.ceil((double) total / size);

        List<Patch> patches = patchRepository.searchPatches(request, page, size);

        Set<UUID> memberPatchIds = userId != null
                ? membershipRepository.findActivePatchIds(userId, patches.stream().map(Patch::getId).toList())
                : Set.of();

        List<PatchSummaryResponse> results = patches.stream()
                .map(p -> mapper.toResponse(p, memberPatchIds.contains(p.getId()), null))
                .toList();

        return SearchResponse.builder()
                .results(results)
                .total(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }
}
