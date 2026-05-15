package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.Patch;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

/**
 * Outbound port (secondary/driven port) that defines the read-side persistence
 * contract for {@link Patch} entities. Provides the query operations required
 * by the feed, search, and recommendation use cases.
 */
public interface PatchRepositoryPort {

    /**
     * Returns all patches that are currently open and publicly visible.
     * Used to populate the default, non-personalised feed.
     *
     * @return a list of open public {@link Patch} records; never {@code null}, may be empty
     */
    List<Patch> findOpenPublicPatches();

    /**
     * Retrieves the patches whose identifiers are included in the provided list.
     * Preserves the caller's ordering where possible.
     *
     * @param ids the list of patch UUIDs to fetch
     * @return a list of matching {@link Patch} records; never {@code null}, may be smaller
     *         than {@code ids} if some identifiers do not correspond to existing patches
     */
    List<Patch> findByIds(List<UUID> ids);

    /**
     * Executes a paginated search query against the patch catalogue using the
     * given filter criteria.
     *
     * @param request the search filter parameters (keywords, category, zone, etc.)
     * @param page    the zero-based page index to retrieve
     * @param size    the maximum number of results per page
     * @return a list of matching {@link Patch} records for the requested page;
     *         never {@code null}, may be empty
     */
    List<Patch> searchPatches(edu.eci.patricia.application.dto.request.SearchRequest request, int page, int size);

    /**
     * Counts the total number of patches that match the given search filter criteria.
     * Used together with {@link #searchPatches} to calculate pagination metadata.
     *
     * @param request the search filter parameters
     * @return the total number of matching patches
     */
    long countPatches(edu.eci.patricia.application.dto.request.SearchRequest request);

    /**
     * Returns the most popular open patches up to the specified limit.
     * Popularity is determined by the infrastructure implementation (e.g., by member count).
     *
     * @param limit the maximum number of patches to return
     * @return a list of popular {@link Patch} records; never {@code null}, may be empty
     */
    List<Patch> findPopularPatches(int limit);

    /**
     * Looks up a single patch by its unique identifier.
     *
     * @param id the unique identifier of the patch to retrieve
     * @return an {@link Optional} containing the {@link Patch} if found, or empty otherwise
     */
    Optional<Patch> findById(UUID id);
}
