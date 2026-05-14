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

/**
 * Infrastructure adapter that bridges the {@link PatchWriteRepositoryPort} domain port
 * and the JPA repositories used for write operations.
 *
 * <p>Handles persisting updated {@link Patch} aggregates and creating new
 * {@link PatchMembership} records when a user joins a patch.</p>
 */
@Component
public class PatchWriteRepositoryAdapter implements PatchWriteRepositoryPort {

    private final PatchJpaRepository           patchRepo;
    private final PatchMembershipJpaRepository membershipRepo;
    private final PatchEntityMapper            mapper;

    /**
     * Constructs the adapter with its required dependencies.
     *
     * @param patchRepo      the JPA repository for {@code patches} table
     * @param membershipRepo the JPA repository for {@code patch_memberships} table
     * @param mapper         the mapper used to convert between domain objects and entities
     */
    public PatchWriteRepositoryAdapter(PatchJpaRepository patchRepo,
                                       PatchMembershipJpaRepository membershipRepo,
                                       PatchEntityMapper mapper) {
        this.patchRepo      = patchRepo;
        this.membershipRepo = membershipRepo;
        this.mapper         = mapper;
    }

    /**
     * Persists a patch aggregate, inserting or updating the underlying record.
     *
     * @param patch the domain object to save
     * @return the saved {@link Patch} domain object after persistence
     */
    @Override
    public Patch save(Patch patch) {
        return mapper.toDomain(patchRepo.save(mapper.toEntity(patch)));
    }

    /**
     * Persists a new membership record with {@code ACTIVE} status and the current timestamp as join time.
     *
     * @param membership the domain object containing membership data (patch ID and user ID)
     * @return the same {@link PatchMembership} domain object passed in, unchanged
     */
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