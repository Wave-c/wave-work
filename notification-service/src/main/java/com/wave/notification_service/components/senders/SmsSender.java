package com.wave.notification_service.components.senders;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.wave.notification_service.dtos.User;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Order(3)
@Component
@RequiredArgsConstructor
public class SmsSender implements INotificationSender {
    private final Unleash unleash;

    @Override
    public Boolean supports(UnleashContext context) {
        return unleash.isEnabled("send-to-sms", context);
    }

    @Override
    public Mono<Void> send(User user, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

}
