package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a full-text search index entry for a patch, stored in the {@code search_index} table.
 *
 * <p>Each row corresponds to one patch and holds a pre-computed search vector (e.g. concatenated
 * title, description, and tags) that can be used to accelerate keyword searches. The
 * {@code lastIndexed} field indicates when the vector was last regenerated.</p>
 */
@Entity
@Table(name = "search_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchIndexEntity {

    @Id
    @Column(name = "patch_id", nullable = false, updatable = false)
    private UUID patchId;

    @Column(name = "search_vector", columnDefinition = "TEXT")
    private String searchVector;

    @Column(name = "last_indexed", nullable = false)
    private LocalDateTime lastIndexed;
}
