package edu.eci.patricia.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "last_index", nullable = false)
    private LocalDateTime lastIndexed;
}