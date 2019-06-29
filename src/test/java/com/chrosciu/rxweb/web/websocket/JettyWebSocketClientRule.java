package com.chrosciu.rxweb.web.websocket;

import org.junit.rules.ExternalResource;
import org.springframework.web.reactive.socket.client.JettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

public class JettyWebSocketClientRule extends ExternalResource {

    private JettyWebSocketClient client;

    @Override
    protected void before() {
        client =  new JettyWebSocketClient();
        client.start();
    }

    @Override
    protected void after() {
        client.stop();
    }

    public WebSocketClient getClient() {
        return client;
    }
}
