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
    //TODO Receive first text message from WebSocket - this will be user login
    //Then search for user with this login in database
    //If found - send user as WebSocket text messages in format "{id} {login}"
    //Hint: use method userRepository#findAll(Example)
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
