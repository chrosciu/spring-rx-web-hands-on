package com.chrosciu.rxweb.web.websocket;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersWebSocketTest {
    @LocalServerPort
    private int port;

    @Test
    @SneakyThrows
    void shouldGetAllUsers() {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(URI.create(String.format("ws://localhost:%d/ws/users", port)), new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {
                return session.receive().map(WebSocketMessage::getPayloadAsText).log().then();
            }
        }).log().doFinally(st -> latch.countDown()).subscribe();
        latch.await();
    }
}
