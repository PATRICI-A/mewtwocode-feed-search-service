package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;
import edu.eci.patricia.domain.model.enums.MembershipStatus;
import edu.eci.patricia.domain.ports.out.PatchWriteRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchMembershipEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.PatchEntityMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchJpaRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.PatchMembershipJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PatchWriteRepositoryAdapter implements PatchWriteRepositoryPort {

    private final PatchJpaRepository           patchRepo;
    private final PatchMembershipJpaRepository membershipRepo;
    private final PatchEntityMapper            mapper;

    public PatchWriteRepositoryAdapter(PatchJpaRepository patchRepo,
                                       PatchMembershipJpaRepository membershipRepo,
                                       PatchEntityMapper mapper) {
        this.patchRepo      = patchRepo;
        this.membershipRepo = membershipRepo;
        this.mapper         = mapper;
    }

    @Override
    public Patch save(Patch patch) {
        return mapper.toDomain(patchRepo.save(mapper.toEntity(patch)));
    }

    @Override
    public PatchMembership saveMembership(PatchMembership membership) {
        PatchMembershipEntity entity = PatchMembershipEntity.builder()
                .id(membership.getId())
                .patchId(membership.getPatchId())
                .userId(membership.getUserId())
                .status(MembershipStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepo.save(entity);
        return membership;
    }
}