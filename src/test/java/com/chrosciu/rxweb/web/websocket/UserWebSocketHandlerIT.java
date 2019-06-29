package com.chrosciu.rxweb.web.websocket;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.ReplayProcessor;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserWebSocketHandlerIT {
    @LocalServerPort
    private String port;

    @Rule
    public JettyWebSocketClientRule jettyWebSocketClientRule = new JettyWebSocketClientRule();

    @Test
    public void testGetUsersByWebSockets() throws Exception {
        ReplayProcessor<String> output = ReplayProcessor.create();
        jettyWebSocketClientRule.getClient().execute(
                getWsUrl("/ws/users"),
                session -> session.receive().map(WebSocketMessage::getPayloadAsText).subscribeWith(output).then()
        ).block();
        StepVerifier.create(output)
                .expectNext("Janusz Cebulak")
                .expectNext("Mirek Handlarz")
                .verifyComplete();
    }

    private URI getWsUrl(String path) throws URISyntaxException {
        return new URI("ws://localhost:" + this.port + path);
    }
}
