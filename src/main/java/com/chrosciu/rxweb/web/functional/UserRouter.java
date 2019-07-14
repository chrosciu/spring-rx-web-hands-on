package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class UserRouter {
    private final UserRepository userRepository;

    @Bean
    RouterFunction<ServerResponse> userRoutes() {
        return null;
    }

    //TODO Create following REST endpoints using functional endpoints
    // 1) GET /functional/users - should return all users
    // 2) GET /functional/users/{id} - should return user with given id
    // 3) POST /functional/users - should save user passed as body
    // 4) DELETE /functional/users/{id} - should delete user with given id
}
