package com.chrosciu.rxweb.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class TickWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> messageFlux = session.receive().map(WebSocketMessage::getPayloadAsText);
        Flux<String> tickFlux = getTickFlux(messageFlux);
        return session.send(tickFlux.map(session::textMessage));
    }

    private Flux<String> getTickFlux(Flux<String> tickIntervalFlux) {
        return tickIntervalFlux.take(1).flatMap(tickIntervalStr -> {
            long interval = Long.parseLong(tickIntervalStr);
            Flux<Long> ticks = Flux.interval(Duration.ofSeconds(interval));
            Flux<String> ticksStr = ticks.map(Object::toString);
            return ticksStr;
        }).onErrorResume(e -> Flux.empty());
    }
}
