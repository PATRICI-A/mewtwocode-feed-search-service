package edu.eci.patricia.infrastructure.adapters.web;

import edu.eci.patricia.application.dto.response.PatchRecommendationResponse;
import edu.eci.patricia.application.mapper.PatchApplicationMapper;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ScoredPatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedUseCase feedUseCase;
    private final PatchRepositoryPort patchRepository;
    private final PatchApplicationMapper mapper;

    public FeedController(FeedUseCase feedUseCase,
                          PatchRepositoryPort patchRepository,
                          PatchApplicationMapper mapper) {
        this.feedUseCase = feedUseCase;
        this.patchRepository = patchRepository;
        this.mapper = mapper;
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<PatchRecommendationResponse>> getRecommendations(
            @RequestParam UUID userId) {

        List<ScoredPatch> scored = feedUseCase.getRecommendations(userId);

        Map<UUID, ?> patchById = patchRepository
                .findByIds(scored.stream().map(ScoredPatch::getPatchId).toList())
                .stream()
                .collect(Collectors.toMap(p -> p.getId(), p -> p));

        List<PatchRecommendationResponse> response = scored.stream()
                .filter(sp -> patchById.containsKey(sp.getPatchId()))
                .map(sp -> {
                    var patch = (edu.eci.patricia.domain.model.Patch) patchById.get(sp.getPatchId());
                    return PatchRecommendationResponse.builder()
                            .patchId(sp.getPatchId())
                            .patch(mapper.toResponse(patch, false, sp.getAffinityScore()))
                            .affinityScore(sp.getAffinityScore())
                            .reason(sp.getReason())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}
