package com.wave.notification_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wave.notification_service.dtos.NotificationChannelDto;
import com.wave.notification_service.models.Notification;
import com.wave.notification_service.services.NotificationSenderService;
import com.wave.notification_service.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationSenderService notificationSenderService;
    private final UserService userService;

    @PostMapping("/send")
    public Mono<ResponseEntity<String>> sendNotification(
        @RequestBody Notification notification) {
        return notificationSenderService.send(notification)
            .thenReturn(ResponseEntity.ok("Notification sent successfully!"))
            .onErrorResume(err -> {
                log.error(err.getMessage());
                return Mono.just(ResponseEntity.status(204)
                    .body("Failed to send notification."));
                });
    }

    @GetMapping("/get-notification-channels")
    public Flux<NotificationChannelDto> getNotificationChannel(@RequestHeader("X-User-Id") UUID userId) {
        return userService.getChannels(userId)
            .map(nc -> new NotificationChannelDto(nc.getType(), nc.getValue()));
    }


}
