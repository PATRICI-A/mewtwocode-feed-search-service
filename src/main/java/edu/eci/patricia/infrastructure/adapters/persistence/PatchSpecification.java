package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for building JPA {@link Specification} predicates used to dynamically filter
 * {@link PatchEntity} records based on the criteria supplied in a {@link SearchRequest}.
 *
 * <p>The class is non-instantiable; use the static factory method {@link #fromRequest(SearchRequest)}.</p>
 */
public class PatchSpecification {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PatchSpecification() {}

    /**
     * Builds a {@link Specification} that combines all non-null search criteria from the request
     * into a single AND predicate. The {@code isPublic} flag is always enforced regardless of
     * other criteria.
     *
     * <p>Supported filters:
     * <ul>
     *   <li>{@code q} — case-insensitive LIKE match on title and description</li>
     *   <li>{@code category} — exact match on patch category</li>
     *   <li>{@code campusZone} — exact match on campus zone</li>
     *   <li>{@code status} — exact match on patch status</li>
     *   <li>{@code dateFrom} — patches starting on or after this date (inclusive)</li>
     *   <li>{@code dateTo} — patches starting on or before this date (inclusive, end of day)</li>
     *   <li>{@code maxGroupSize} — patches whose capacity is at most this value</li>
     *   <li>{@code hasAvailableSpots} — patches where {@code currentCount < capacity}</li>
     * </ul>
     * </p>
     *
     * @param request the search request containing the filter criteria; must not be {@code null}
     * @return a {@link Specification} instance that can be passed to
     *         {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor}
     */
    public static Specification<PatchEntity> fromRequest(SearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isPublic")));

            if (request.getQ() != null && !request.getQ().isBlank()) {
                String pattern = "%" + request.getQ().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (request.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), request.getCategory()));
            }

            if (request.getCampusZone() != null) {
                predicates.add(cb.equal(root.get("campusZone"), request.getCampusZone()));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("startTime"), request.getDateFrom().atStartOfDay()
                ));
            }

            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("startTime"), request.getDateTo().atTime(LocalTime.MAX)
                ));
            }

            if (request.getMaxGroupSize() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), request.getMaxGroupSize()));
            }

            if (Boolean.TRUE.equals(request.getHasAvailableSpots())) {
                predicates.add(cb.lessThan(root.get("currentCount"), root.get("capacity")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
