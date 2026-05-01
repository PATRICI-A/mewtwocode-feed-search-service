package edu.eci.patricia.application.usecase;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.ports.in.SearchPatchesUseCase;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<PatchSummaryResponse> execute(UUID userId, SearchRequest request, int page, int size) {
        return patchRepository.searchPatches(request).stream()
                .map(p -> mapper.toResponse(p, membershipRepository.existsActiveMembership(p.getId(), userId), null))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }
}
