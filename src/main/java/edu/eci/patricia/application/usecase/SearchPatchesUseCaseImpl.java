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

@Service
public class SearchPatchesUseCaseImpl implements SearchPatchesUseCase {

    private final PatchRepositoryPort patchRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchDomainMapper mapper;

    public SearchPatchesUseCaseImpl(PatchRepositoryPort patchRepository,
                                   MembershipRepositoryPort membershipRepository,
                                   PatchDomainMapper mapper) {
        this.patchRepository = patchRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
    }

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
