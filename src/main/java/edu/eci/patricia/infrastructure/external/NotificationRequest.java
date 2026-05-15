package edu.eci.patricia.infrastructure.external;

import java.util.UUID;

public record NotificationRequest(
        UUID userId,
        String type,
        String title,
        String body,
        String referenceId) {

}