package com.chrosciu.rxweb.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class UpperCaseWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(String::toUpperCase)
                .map(session::textMessage));
    }
}
