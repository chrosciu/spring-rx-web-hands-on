package com.chrosciu.rxweb.web.websocket;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static com.chrosciu.rxweb.util.Users.JANUSZ;
import static com.chrosciu.rxweb.util.Users.MIREK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT;

@RunWith(MockitoJUnitRunner.class)
public class UserWebSocketHandlerTest {
    private UserRepository userRepository;
    private UserWebSocketHandler userWebSocketHandler;
    private WebSocketSession webSocketSession;

    @Captor
    private ArgumentCaptor<Flux<WebSocketMessage>> captor;

    @Before
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        webSocketSession = Mockito.mock(WebSocketSession.class);
        userWebSocketHandler = new UserWebSocketHandler(userRepository);
    }

    @Test
    public void testGetAllUsersByWebSockets() {
        //given
        Mono<Void> result = Mono.empty();
        when(userRepository.findAll()).thenReturn(Flux.just(JANUSZ, MIREK));
        when(webSocketSession.textMessage(anyString()))
                .thenAnswer((Answer<WebSocketMessage>) invocation -> createTextWebSocketMessage(invocation.getArgument(0)));
        when(webSocketSession.send(any())).thenReturn(result);
        //when
        Mono<Void> actualResult = userWebSocketHandler.handle(webSocketSession);
        //then
        assertEquals(result, actualResult);
        verify(webSocketSession).send(captor.capture());
        StepVerifier.create(captor.getValue())
                .assertNext(assertPayloadEquals(JANUSZ.getFirstName() + " " + JANUSZ.getLastName()))
                .assertNext(assertPayloadEquals(MIREK.getFirstName() + " " + MIREK.getLastName()))
                .verifyComplete();
    }

    private static WebSocketMessage createTextWebSocketMessage(@NonNull String payload) {
        return new WebSocketMessage(TEXT, new DefaultDataBufferFactory().wrap(payload.getBytes(UTF_8)));
    }

    private static Consumer<WebSocketMessage> assertPayloadEquals(String expectedPayload) {
        return webSocketMessage -> assertEquals(expectedPayload, webSocketMessage.getPayloadAsText());
    }
}
