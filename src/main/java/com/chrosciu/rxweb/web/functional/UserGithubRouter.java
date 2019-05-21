package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.repository.UserRepository;
import com.chrosciu.rxweb.web.client.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class UserGithubRouter {
    private final UserRepository userRepository;
    private final GithubClient githubClient;

    @Bean
    RouterFunction<ServerResponse> userGithubRoutes() {
        return route(GET("/users/repos"), this::getAllUsersRepos);
    }

    private Mono<ServerResponse> getAllUsersRepos(ServerRequest request) {
        return ServerResponse.ok().body(userRepository.findAll().flatMap(user -> githubClient.getUserRepos(user.getGithubName())), GithubRepo.class);
    }
}
