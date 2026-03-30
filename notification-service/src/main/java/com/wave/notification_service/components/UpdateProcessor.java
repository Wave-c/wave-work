package com.wave.notification_service.components;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.wave.notification_service.components.commands.ICommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final List<ICommand> commands;

    public Mono<Void> processUpdate(Update update) {
        if (update == null) {
            log.warn("Received null update");
            return Mono.empty();
        }

        return distributeMessage(update);
    }

    private Mono<Void> distributeMessage(Update update) {
        return Flux.fromIterable(commands)
            .filter(cmd -> cmd.canExecute(update))
            .next()
            .flatMap(cmd -> {
                if(update.hasMessage())
                {
                    return cmd.execute(update.getMessage())
                        .onErrorResume(err -> Mono.empty());
                }
                else if (update.hasCallbackQuery())
                {
                    return cmd.execute(update.getCallbackQuery())
                        .onErrorResume(err -> Mono.empty());
                }
                else {
                    return Mono.empty();
                }
            });
    }
}
