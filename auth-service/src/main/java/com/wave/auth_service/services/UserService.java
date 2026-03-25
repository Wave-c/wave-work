package com.wave.auth_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wave.auth_service.dtos.RegistrationRequest;
import com.wave.auth_service.helpers.exceptions.UserAlreadyExistException;
import com.wave.auth_service.models.AuthUser;
import com.wave.auth_service.models.AuthUser.UserRole;
import com.wave.auth_service.models.AuthUser.UserStatus;
import com.wave.auth_service.repositories.AuthUserRepository;
import com.wave.auth_service.repositories.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthUserRepository authUserRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username)
            .flatMap(user ->
                roleRepository.findRolesByUserId(user.getUserId())
                    .map(UserRole::valueOf)
                    .collectList()
                    .map(roles -> user.setRoles(roles))
            );
    }

    @Transactional
    public Mono<AuthUser> registerUser(RegistrationRequest regRequest) {
        UUID userId = UUID.randomUUID();

        AuthUser newUser = AuthUser.builder()
            .userId(userId)
            .username(regRequest.getUsername())
            .passwordHash(passwordEncoder.encode(regRequest.getPassword()))
            .status(UserStatus.ACTIVE)
            .createdAt(Instant.now())
            .isNew(true)
            .build();

        return findByUsername(regRequest.getUsername())
            .flatMap(existingUser ->
                Mono.<AuthUser>error(new UserAlreadyExistException("Username already exist"))
            )
            .switchIfEmpty(
                authUserRepository.save(newUser)
                    .flatMap(savedUser ->
                        roleRepository.insertRoles(savedUser.getUserId(), List.of(UserRole.USER))
                            .then(Mono.just(savedUser))
                    )
            );
    }

    public Mono<List<String>> getRoles(UUID userId) {
        return roleRepository.findRolesByUserId(userId)
            .collectList();
    }

    public Boolean validateUser(AuthUser user, String password) {
        return passwordEncoder.matches(password, user.getPassword()) &&
            user.getStatus() != UserStatus.BANNED;
    }
}
