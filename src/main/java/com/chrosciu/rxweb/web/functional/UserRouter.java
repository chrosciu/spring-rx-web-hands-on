package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.model.User;
import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class UserRouter {
    private final UserRepository userRepository;

    @Bean
    RouterFunction<ServerResponse> userRoutes() {
        return route(GET("/functional/users"), this::getAllUsers)
                .and(route(GET("/functional/users/{id}"), this::getUser))
                .and(route(POST("/functional/users"), this::addUser))
                .and(route(DELETE("/functional/users/{id}"), this::deleteUser));
    }

    private Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok().body(userRepository.findAll(), User.class);
    }

    private Mono<ServerResponse> getUser(ServerRequest request) {
        return ServerResponse.ok().body(userRepository.findById(request.pathVariable("id")), User.class);
    }

    private Mono<ServerResponse> addUser(ServerRequest request) {
        return ServerResponse.ok().body(request.bodyToMono(User.class).doOnNext(userRepository::save), User.class);
    }

    private Mono<ServerResponse> deleteUser(ServerRequest request) {
        return userRepository.deleteById(request.pathVariable("id")).then(ServerResponse.ok().build());
    }

}
