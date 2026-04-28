package edu.eci.patricia.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RecommendationResult{
    private UUID userId;
    private LocalDateTime generatedAt;
}