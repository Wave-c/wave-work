CREATE TABLE notification_channel (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL,

    type            VARCHAR(32) NOT NULL,
    value           TEXT NOT NULL,

    verified        BOOLEAN NOT NULL DEFAULT FALSE,

    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Ограничение: один канал конкретного типа на пользователя
ALTER TABLE notification_channel
    ADD CONSTRAINT uq_user_type UNIQUE (user_id, type);

-- Уникальность значения (например, один email = один аккаунт)
ALTER TABLE notification_channel
    ADD CONSTRAINT uq_type_value UNIQUE (type, value);

-- Индекс для быстрого поиска по userId
CREATE INDEX idx_notification_channel_user_id
    ON notification_channel(user_id);

-- Индекс для поиска по type (например, все telegram)
CREATE INDEX idx_notification_channel_type
    ON notification_channel(type);