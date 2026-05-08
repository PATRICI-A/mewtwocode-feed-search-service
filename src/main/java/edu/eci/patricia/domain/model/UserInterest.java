package edu.eci.patricia.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterest {
    private UUID id;
    private UUID userId;
    private String interestingTag;
    private LocalDateTime createdAt;
}
