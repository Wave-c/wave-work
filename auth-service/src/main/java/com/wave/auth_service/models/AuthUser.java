package com.wave.auth_service.models;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Table("auth_user")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthUser implements UserDetails, Persistable<UUID> {
    @Id
    private UUID userId;
    private String username;
    private String passwordHash;
    private UserStatus status;
    private Instant createdAt;

    @Transient
    private List<UserRole> roles;
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> "ROLE_" + role.name())
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public @Nullable UUID getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        BANNED
    }

    public enum UserRole {
        USER,
        ADMIN;
    }


}
