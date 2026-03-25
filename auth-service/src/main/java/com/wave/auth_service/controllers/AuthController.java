package com.wave.auth_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.wave.auth_service.dtos.LoginRequest;
import com.wave.auth_service.dtos.RegistrationRequest;
import com.wave.auth_service.services.JwtService;
import com.wave.auth_service.services.RefreshTokenStorageService;
import com.wave.auth_service.services.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final RefreshTokenStorageService refreshTokenStorageService;
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(
        @RequestBody LoginRequest request,
        ServerWebExchange exchange) {
        return userService.findByUsername(request.getUsername())
            .flatMap(user ->
                userService.validateUser(user, request.getPassword()) ?
                    Mono.zip(
                        jwtService.generateAccessToken(
                            user.getUserId(),
                            List.of("USER")),
                        jwtService.generateRefreshToken(user.getUserId()))
                        .map(tupple -> {
                            ResponseCookie accessCookie =
                                buildCookie("access_token", tupple.getT1(),
                                    Duration.ofMinutes(15));
                            ResponseCookie refreshCookie =
                                buildCookie("refresh_token", tupple.getT2(),
                                    Duration.ofDays(7));
                            exchange.getResponse().addCookie(accessCookie);
                            exchange.getResponse().addCookie(refreshCookie);

                            return ResponseEntity.ok(
                                Map.of("userId", user.getUserId().toString())
                            );
                        }) :
                    Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                            Map.of("message",
                            "Invalid login or password")))
            ).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                            Map.of("message",
                            "Invalid login or password"))));
    }

    @PostMapping("/registration")
    public Mono<ResponseEntity<Map<String, String>>> registration(
        @RequestBody RegistrationRequest request,
        ServerWebExchange exchange) {
        return userService.registerUser(request)
            .then(login(new LoginRequest(request.getUsername(), request.getPassword()),
                exchange))
            .onErrorReturn(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                    Map.of("message",
                    "Username already exists")));
    }


    @PostMapping("/refresh")
    public Mono<ResponseEntity<Void>> refresh(ServerWebExchange exchange) {
        HttpCookie refreshCookie =
            exchange.getRequest().getCookies().getFirst("refresh_token");

        if (refreshCookie == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build());
        }

        return refreshTokenStorageService.getUserId(refreshCookie.getValue())
            .flatMap(userId -> userService.getRoles(userId)
                .flatMap(roles ->
                 jwtService.generateAccessToken(
                    userId, roles)
                    .flatMap(newAccess -> {
                        ResponseCookie accessCookie =
                            buildCookie("access_token", newAccess,
                                Duration.ofMinutes(15));
                        exchange.getResponse().addCookie(accessCookie);

                        return Mono.just(ResponseEntity.ok().build());
                    }))

            );
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {
        HttpCookie refreshCookie = exchange.getRequest().getCookies().getFirst("refresh_token");
        String refreshToken = (refreshCookie != null) ? refreshCookie.getValue() : null;

        return refreshTokenStorageService.delete(refreshToken)
            .flatMap(deleted -> {
                ResponseCookie clearJwt = buildCookie("access_token",
                    "", Duration.ZERO
                );
                ResponseCookie clearRefresh = buildCookie("refresh_token",
                    "", Duration.ZERO
                );

                exchange.getResponse().addCookie(clearJwt);
                exchange.getResponse().addCookie(clearRefresh);

                return Mono.just(ResponseEntity.noContent().build());
            });
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        ResponseCookieBuilder builder = ResponseCookie.from(name, value)
            .path("/")
            .sameSite("Strict")
            .maxAge(maxAge);

        if ("prod".equals(activeProfile)) {
            builder.secure(true).httpOnly(true);
        }

        return builder.build();
    }
}
