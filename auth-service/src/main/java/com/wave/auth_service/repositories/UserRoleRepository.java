package com.wave.auth_service.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.wave.auth_service.models.AuthUser.UserRole;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class UserRoleRepository {
    private final DatabaseClient databaseClient;

    public Flux<String> findRolesByUserId(UUID userId) {
        return databaseClient.sql("""
                SELECT role
                FROM user_roles
                WHERE user_id = :userId
                """)
            .bind("userId", userId)
            .map(row -> row.get("role", String.class))
            .all();
    }

    public Flux<Void> insertRoles(UUID userId, List<UserRole> roles) {
        return Flux.fromIterable(roles)
            .flatMap(role ->
                databaseClient.sql("""
                        INSERT INTO user_roles (user_id, role)
                        VALUES (:userId, :role)
                        """)
                    .bind("userId", userId)
                    .bind("role", role.name())
                    .then()
            );
    }
}
