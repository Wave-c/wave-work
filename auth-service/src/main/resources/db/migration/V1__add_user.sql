CREATE TABLE auth_user (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES auth_user(user_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);