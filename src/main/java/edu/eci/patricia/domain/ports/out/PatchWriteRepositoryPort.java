package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;
import edu.eci.patricia.domain.model.PatchMembership;

public interface PatchWriteRepositoryPort {
    Patch save(Patch patch);
    PatchMembership saveMembership(PatchMembership membership);
}