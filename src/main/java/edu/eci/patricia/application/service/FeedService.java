package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.response.PatchSummaryResponse;
import edu.eci.patricia.application.mapper.PatchDomainMapper;
import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.UserInterest;
import edu.eci.patricia.domain.ports.in.FeedUseCase;
import edu.eci.patricia.domain.ports.out.MembershipRepositoryPort;
import edu.eci.patricia.domain.ports.out.PatchRepositoryPort;
import edu.eci.patricia.domain.ports.out.UserInterestRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService implements FeedUseCase {

    // RF14: intereses 50% + proximidad geográfica 30% (pendiente Feign) + cercanía temporal 20%
    private static final float INTEREST_SCORE     = 0.50f;
    private static final float TEMPORAL_SCORE_MAX = 0.20f;
    // GEO_SCORE_MAX = 0.30f — se integrará vía Feign al servicio de geolocalización

    private final PatchRepositoryPort patchRepository;
    private final UserInterestRepositoryPort interestRepository;
    private final MembershipRepositoryPort membershipRepository;
    private final PatchDomainMapper mapper;

    public FeedService(PatchRepositoryPort patchRepository,
                       UserInterestRepositoryPort interestRepository,
                       MembershipRepositoryPort membershipRepository,
                       PatchDomainMapper mapper) {
        this.patchRepository = patchRepository;
        this.interestRepository = interestRepository;
        this.membershipRepository = membershipRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PatchSummaryResponse> execute(UUID userId, int page, int size) {
        Set<String> interestTags = interestRepository.findByUserId(userId).stream()
                .map(UserInterest::getInterestingTag)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return patchRepository.findOpenPublicPatches().stream()
                .map(p -> {
                    float score = scoreRelevance(p, interestTags);
                    boolean isMember = membershipRepository.existsActiveMembership(p.getId(), userId);
                    return mapper.toResponse(p, isMember, score);
                })
                .sorted(Comparator.comparing(
                        PatchSummaryResponse::getAffinityScore,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    private float scoreRelevance(Patch patch, Set<String> interestTags) {
        float score = 0f;

        // Intereses del usuario: 50%
        if (interestTags.contains(patch.getCategory().name())) {
            score += INTEREST_SCORE;
        }

        // Cercanía temporal: 20% (parches más próximos en el tiempo puntúan más alto)
        score += temporalScore(patch.getStartTime());

        // Proximidad geográfica: 30% — pendiente integración con servicio de geolocalización

        return score;
    }

    private float temporalScore(LocalDateTime startTime) {
        if (startTime == null) return 0f;
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), startTime);
        if (hours < 0)    return 0f;                          // parche ya empezó
        if (hours <= 24)  return TEMPORAL_SCORE_MAX;          // hoy / mañana
        if (hours <= 72)  return TEMPORAL_SCORE_MAX * 0.75f;  // próximos 3 días
        if (hours <= 168) return TEMPORAL_SCORE_MAX * 0.50f;  // próxima semana
        if (hours <= 720) return TEMPORAL_SCORE_MAX * 0.25f;  // próximo mes
        return 0f;
    }
}
