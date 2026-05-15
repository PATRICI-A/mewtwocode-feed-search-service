package edu.eci.patricia.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "m05-notification-service",
        url = "${services.m05.url}"
)
public interface NotificationFeignClient {

    @PostMapping("/api/notifications")
    void sendNotification(
            @RequestBody NotificationRequest request
    );
}