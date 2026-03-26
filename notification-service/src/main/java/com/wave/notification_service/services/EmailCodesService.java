package com.wave.notification_service.services;

import java.security.KeyException;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.stereotype.Service;

import com.wave.notification_service.dtos.EmailCodes;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class EmailCodesService {
    private final ReactiveValueOperations<String, EmailCodes> valueOps;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailCodesService(
        @Qualifier("emailCodesOperations")
        ReactiveRedisOperations<String, EmailCodes> ops,
        JavaMailSender mailSender) {
        valueOps = ops.opsForValue();
        this.mailSender = mailSender;
    }

    public Mono<Boolean> set(String key, EmailCodes value) {
        return valueOps.set(key, value, Duration.ofDays(1));
    }

    public Mono<EmailCodes> getByKey(String key) {
        return valueOps.get(key)
            .switchIfEmpty(Mono.error(new KeyException("Key not found")));
    }

    public Mono<Void> deleteByKey(String key) {
        return valueOps.delete(key)
            .flatMap(success -> success ?
                Mono.empty() :
                Mono.error(new ValidationFailureException("Bad id")));
    }

    public Mono<String> getNew(UUID userId, String email) {
        Random random = new Random();
        int code = random.nextInt(10000);
        String formattedCode = String.format("%04d", code);

        log.info("Sending code to email");


        return set(userId.toString(), new EmailCodes(
            formattedCode, email
        ))
            .then(Mono.fromRunnable(() -> {
                SimpleMailMessage emailMessage = new SimpleMailMessage();
                emailMessage.setFrom(mailFrom);
                emailMessage.setTo(email);
                emailMessage.setSubject("WaveWork код подтверждения");
                emailMessage.setText(
                    "Ваш код подтверждения почты %s".formatted(formattedCode));
                mailSender.send(emailMessage);
            }))
            .thenReturn(userId.toString());
    }
}
