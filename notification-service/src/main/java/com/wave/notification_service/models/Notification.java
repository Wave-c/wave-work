package com.wave.notification_service.models;

import com.wave.notification_service.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private NotificationType type;
    private User recipient;
}
