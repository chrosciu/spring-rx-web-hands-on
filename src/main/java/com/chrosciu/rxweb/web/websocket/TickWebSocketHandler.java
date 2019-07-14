package com.chrosciu.rxweb.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class TickWebSocketHandler implements WebSocketHandler {
    //TODO Receive first text message from WebSocket, parse it as number
    //Then send infinite stream of numbers with interval in seconds equal to parsed number (tick stream)
    //If received message is not a valid number do not send anything
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
