package com.chrosciu.rxweb.web.websocket;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;

    @Override
    //TODO Send all users as WebSocket text messages in format "{firstName} {lastName}"
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
