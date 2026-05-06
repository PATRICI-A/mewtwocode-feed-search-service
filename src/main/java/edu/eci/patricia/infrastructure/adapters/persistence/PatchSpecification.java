package edu.eci.patricia.infrastructure.adapters.persistence;

import edu.eci.patricia.application.dto.request.SearchRequest;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.PatchEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PatchSpecification {

    private PatchSpecification() {}

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
