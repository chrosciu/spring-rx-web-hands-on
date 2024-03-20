package eu.chrost.rxweb.web.websocket;

import eu.chrost.rxweb.repository.UserRepository;
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
    //TODO Receive text messages from WebSocket
    //For each message - read the body as user login and search for user with this login in database
    //If found - send user as WebSocket text messages in format "{id} {login}"
    //Hint: use method userRepository#findAll(Example)
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
