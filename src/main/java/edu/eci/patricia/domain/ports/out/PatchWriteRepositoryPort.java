package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;

/**
 * Outbound port (secondary/driven port) that defines the write-side persistence
 * contract for {@link Patch} and {@link PatchMembership} entities. Separates
 * command (write) operations from query (read) operations following the CQRS principle.
 */
public interface PatchWriteRepositoryPort {

    /**
     * Persists (inserts or updates) the given patch.
     *
     * @param patch the {@link Patch} to save
     * @return the saved {@link Patch}, potentially with updated persistence metadata
     *         such as a generated identifier or audit timestamps
     */
    Patch save(Patch patch);

    /**
     * Persists a new membership record linking a user to a patch.
     *
     * @param membership the {@link PatchMembership} to save
     * @return the saved {@link PatchMembership}, potentially with a generated identifier
     */
    PatchMembership saveMembership(PatchMembership membership);
}