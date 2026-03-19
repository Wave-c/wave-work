package com.wave.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.notification_service.models.Notification;
import com.wave.notification_service.services.NotificationSenderService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationSenderService notificationSenderService;

    @PostMapping("/send")
    public Mono<ResponseEntity<String>> sendNotification(
        @RequestBody Notification notification) {
        return notificationSenderService.send(notification)
            .thenReturn(ResponseEntity.ok("Notification sent successfully!"))
            .onErrorReturn(ResponseEntity.status(500)
                .body("Failed to send notification."));
    }

}
