package eu.chrost.rxweb.web.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {
    private final UsersWebSocketHandler usersWebSocketHandler;
    private final UserWebSocketHandler userWebSocketHandler;
    private final UpperCaseEchoWebSocketHandler upperCaseEchoWebSocketHandler;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        //map.put("/ws/users", usersWebSocketHandler);
        //map.put("/ws/user", userWebSocketHandler);
        map.put("/ws/upper-case-echo", upperCaseEchoWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1); // before annotated controllers
        return mapping;
    }
}
