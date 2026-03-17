package com.wave.auth_service.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
public class JwksController {
    private final JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    public Mono<Map<String, Object>> jwks() {
        return Mono.just(jwkSet.toJSONObject());
    }

}
