package com.chrosciu.rxweb.web.websocket;

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

import java.time.Duration;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT;

@RunWith(MockitoJUnitRunner.class)
public class TickWebSocketHandlerTest {
    private TickWebSocketHandler tickWebSocketHandler;
    private WebSocketSession webSocketSession;

    @Captor
    private ArgumentCaptor<Flux<WebSocketMessage>> captor;

    @Before
    public void setup() {
        webSocketSession = Mockito.mock(WebSocketSession.class);
        tickWebSocketHandler = new TickWebSocketHandler();
    }

    @Test
    public void testGetTicksByWebSockets() {
        //given
        Mono<Void> result = Mono.empty();
        when(webSocketSession.send(any())).thenReturn(result);
        when(webSocketSession.textMessage(anyString()))
                .thenAnswer((Answer<WebSocketMessage>) invocation -> createTextWebSocketMessage(invocation.getArgument(0)));
        when(webSocketSession.receive()).thenReturn(Flux.just(createTextWebSocketMessage("2")));
        //when
        Mono<Void> actualResult = tickWebSocketHandler.handle(webSocketSession);
        //then
        assertEquals(result, actualResult);
        verify(webSocketSession).send(captor.capture());
        StepVerifier.create(captor.getValue(), 0)
                .thenRequest(2)
                .thenAwait(Duration.ofSeconds(2))
                .assertNext(assertPayloadEquals("0"))
                .thenAwait(Duration.ofSeconds(2))
                .assertNext(assertPayloadEquals("1"))
                .thenCancel()
                .verify();

    }

    @Test
    public void testGetTicksByWebSocketsWithImproperParam() {
        //given
        Mono<Void> result = Mono.empty();
        when(webSocketSession.send(any())).thenReturn(result);
        when(webSocketSession.receive()).thenReturn(Flux.just(createTextWebSocketMessage("bad")));
        //when
        Mono<Void> actualResult = tickWebSocketHandler.handle(webSocketSession);
        //then
        assertEquals(result, actualResult);
        verify(webSocketSession).send(captor.capture());
        StepVerifier.create(captor.getValue(), 0)
                .verifyComplete();

    }

    private static WebSocketMessage createTextWebSocketMessage(@NonNull String payload) {
        return new WebSocketMessage(TEXT, new DefaultDataBufferFactory().wrap(payload.getBytes(UTF_8)));
    }

    private static Consumer<WebSocketMessage> assertPayloadEquals(String expectedPayload) {
        return webSocketMessage -> assertEquals(expectedPayload, webSocketMessage.getPayloadAsText());
    }
}
