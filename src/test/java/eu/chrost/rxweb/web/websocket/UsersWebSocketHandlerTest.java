package eu.chrost.rxweb.web.websocket;

import eu.chrost.rxweb.repository.UserRepository;
import eu.chrost.rxweb.data.TestUsers;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT;

@ExtendWith(MockitoExtension.class)
public class UsersWebSocketHandlerTest {
    private UserRepository userRepository;
    private UsersWebSocketHandler usersWebSocketHandler;
    private WebSocketSession webSocketSession;

    @Captor
    private ArgumentCaptor<Flux<WebSocketMessage>> captor;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        webSocketSession = Mockito.mock(WebSocketSession.class);
        usersWebSocketHandler = new UsersWebSocketHandler(userRepository);
    }

    @Test
    public void testGetAllUsersByWebSockets() {
        //given
        Mono<Void> result = Mono.empty();
        when(userRepository.findAll()).thenReturn(Flux.just(TestUsers.CHROSCIU, TestUsers.OCTOCAT));
        when(webSocketSession.textMessage(anyString()))
                .thenAnswer((Answer<WebSocketMessage>) invocation -> createTextWebSocketMessage(invocation.getArgument(0)));
        when(webSocketSession.send(any())).thenReturn(result);
        //when
        Mono<Void> actualResult = usersWebSocketHandler.handle(webSocketSession);
        //then
        assertEquals(result, actualResult);
        verify(webSocketSession).send(captor.capture());
        StepVerifier.create(captor.getValue())
                .assertNext(assertPayloadEquals(TestUsers.CHROSCIU.getId() + " " + TestUsers.CHROSCIU.getLogin()))
                .assertNext(assertPayloadEquals(TestUsers.OCTOCAT.getId() + " " + TestUsers.OCTOCAT.getLogin()))
                .verifyComplete();
    }

    private static WebSocketMessage createTextWebSocketMessage(@NonNull String payload) {
        return new WebSocketMessage(TEXT, new DefaultDataBufferFactory().wrap(payload.getBytes(UTF_8)));
    }

    private static Consumer<WebSocketMessage> assertPayloadEquals(String expectedPayload) {
        return webSocketMessage -> assertEquals(expectedPayload, webSocketMessage.getPayloadAsText());
    }
}
