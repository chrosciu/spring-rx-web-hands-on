package eu.chrost.rxweb.web.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UpperCaseEchoWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        //receive messages
        Flux<String> receivedMessages = session.receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .doOnNext(message -> log.info("Message received: {}", message));

        //process received messages
        Flux<String> processedMessages = receivedMessages.map(message -> message.toUpperCase());

        //send back processed messages
        Flux<WebSocketMessage> messagesToSend = processedMessages
                .doOnNext(message -> log.info("Message to be sent: {}", message))
                .map(message -> session.textMessage(message));
        Mono<Void> sendResult = session.send(messagesToSend);

        //wait for send (receive is not needed as send depends on it)
        return sendResult;
    }
}
