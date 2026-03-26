package com.wave.notification_service.components.senders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.wave.notification_service.models.NotificationChannel;
import com.wave.notification_service.models.NotificationChannelType;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
@Order(2)
@Component
@RequiredArgsConstructor
public class EmailSender implements INotificationSender {
    private final Unleash unleash;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailFrom;

    @Override
    public Boolean supports(UnleashContext context) {
        return unleash.isEnabled("send-to-email", context);
    }

    @Override
    public Mono<Void> send(NotificationChannel channel, String message) {
        log.info("Sending to email");
        return Mono.fromRunnable(() -> {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setFrom(mailFrom);
            emailMessage.setTo(channel.getValue());
            emailMessage.setSubject("WaveWork уведомление");
            emailMessage.setText(message);
            mailSender.send(emailMessage);
        });
    }

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.EMAIL;
    }

}
