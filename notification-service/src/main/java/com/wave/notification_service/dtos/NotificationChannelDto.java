package com.wave.notification_service.dtos;

import com.wave.notification_service.models.NotificationChannelType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannelDto {
    private NotificationChannelType type;
    private String value;
}
