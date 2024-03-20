package eu.chrost.rxweb.web.websocket;

import eu.chrost.rxweb.model.User;
import eu.chrost.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserWebSocketHandler implements WebSocketHandler {
    private final UserRepository userRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<String> loginFlux = session.receive().map(WebSocketMessage::getPayloadAsText);
        Flux<String> userFlux = getUserFlux(loginFlux);
        return session.send(userFlux.map(session::textMessage));
    }

    private Flux<String> getUserFlux(Flux<String> loginFlux) {
        return loginFlux.flatMap(login ->
                userRepository.findAll(buildExampleByLogin(login)).map(this::getUserAsText)
        );
    }

    private Example<User> buildExampleByLogin(String login) {
        return Example.of(User.builder().login(login).build());
    }

    private String getUserAsText(User user) {
        return user.getId() + " " + user.getLogin();
    }
}
