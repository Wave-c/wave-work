package com.wave.notification_service.models;

public enum NotificationType {
    LIKE("Ваша задача \"%s\" понравилась!"),
    RESPOND("На вашу задачу \"%s\" откликнулись!"),
    APPLICATION_APPROVED("Ваш отклик, на задачу \"%s\", принят"),
    TASK_READY("Ваша задача, \"%s\" готова"),
    TASK_CANCELED("Задача \"%s\" отменена")
    ;

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
