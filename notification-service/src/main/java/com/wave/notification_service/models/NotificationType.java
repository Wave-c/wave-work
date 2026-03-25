package com.wave.notification_service.models;

public enum NotificationType {
    LIKE("Вашу задача \"%s\" понравилась!"),
    RESPOND("На вашу задачу \"%s\" откликнулись!"),
    TASK_READY("Ваша задача готова"),
    TASK_CANCELED("Задача отменена")
    ;

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
