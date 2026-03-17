package com.wave.helloworld_test_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Mono<ResponseEntity<String>> sayHello(ServerWebExchange exchange) {
        System.out.println("Headers: " + exchange.getRequest().getHeaders());
        return Mono.just(ResponseEntity.ok("Hello, World!"));
    }
}
