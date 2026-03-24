package com.wave.notification_service.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannel {
    private UUID id;
    private UUID userId;
    private NotificationChannelType type;
    private String value;
    private Boolean verified;
}
