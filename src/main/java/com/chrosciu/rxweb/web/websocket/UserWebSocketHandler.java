package com.chrosciu.rxweb.web.websocket;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(getUsersAsText().map(session::textMessage));
    }

    private Flux<String> getUsersAsText() {
        return userRepository.findAll().map(this::getUserAsText);
    }

    private String getUserAsText(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
