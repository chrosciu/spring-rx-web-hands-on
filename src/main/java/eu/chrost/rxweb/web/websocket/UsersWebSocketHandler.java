package eu.chrost.rxweb.web.websocket;

import eu.chrost.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsersWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;

    @Override
    //TODO Send all users as WebSocket text messages in format "{id} {login}"
    public Mono<Void> handle(WebSocketSession session) {
        return null;
    }
}
