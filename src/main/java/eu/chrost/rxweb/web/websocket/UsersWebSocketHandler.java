package eu.chrost.rxweb.web.websocket;

import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsersWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(getUsersAsText().map(session::textMessage));
    }

    private Flux<String> getUsersAsText() {
        return userRepository.findAll().map(this::getUserAsText);
    }

    private String getUserAsText(User user) {
        return user.getId() + " " + user.getLogin();
    }
}
