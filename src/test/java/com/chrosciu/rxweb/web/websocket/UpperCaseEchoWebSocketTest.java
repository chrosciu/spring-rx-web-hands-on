package com.chrosciu.rxweb.web.websocket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.testcontainers.containers.MongoDBContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UpperCaseEchoWebSocketTest {
    @LocalServerPort
    private int port;

    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.2").withReuse(true);

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
    }

    @Test
    void shouldTransformInputToUpperCase() {
        Flux<String> resultFlux = Flux.create(new Consumer<FluxSink<String>>() {
            @Override
            @SneakyThrows
            public void accept(FluxSink<String> sink) {
                CountDownLatch latch = new CountDownLatch(1);
                WebSocketClient client = new ReactorNettyWebSocketClient();
                URI url = URI.create(String.format("ws://localhost:%d/ws/upper-case-echo", port));
                Mono<Void> clientResult = client.execute(url, new WebSocketHandler() {
                    @Override
                    public Mono<Void> handle(WebSocketSession session) {
                        Flux<String> messagesToSend = Flux.just("Marcin", "Tomasz");
                        Flux<WebSocketMessage> webSocketMessagesToSend = messagesToSend
                                .map(message -> session.textMessage(message));
                        Mono<Void> sendResult = session.send(webSocketMessagesToSend);
                        Flux<WebSocketMessage> receivedWebSocketMessages = session.receive().take(2);
                        Flux<String> receivedMessages = receivedWebSocketMessages
                                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                                .doOnNext(message -> {
                                    log.info("Received message: {}", message);
                                    sink.next(message);
                                })
                                .doOnComplete(() -> sink.complete());
                        Mono<Void> result = Mono.when(sendResult, receivedMessages);
                        return result;
                    }
                });
                clientResult.doFinally(st -> latch.countDown()).subscribe();
                latch.await();
            }
        });

        StepVerifier.create(resultFlux)
                .expectNext("MARCIN")
                .expectNext("TOMASZ")
                .verifyComplete();

    }
}
