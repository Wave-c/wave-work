package com.wave.notification_service.models;

public enum NotificationType {
    LIKE("Вашу задача \"%s\" понравилась!"),
    RESPOND("На вашу задачу \"%s\" откликнулись!")
    ;

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
