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

public class UserInterest{
    private UUID id;
    private UUID userId;
    private String interestingTag;
    private LocalDateTime createdAt;
}