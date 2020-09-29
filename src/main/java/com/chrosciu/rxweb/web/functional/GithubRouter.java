package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.User;
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
public class GithubRouter {
    private final UserRepository userRepository;
    private final GithubClient githubClient;

    @Bean
    RouterFunction<ServerResponse> githubRoutes() {
        return route(GET("/functional/github/users/repos"), this::getAllUsersGithubRepos)
                .and(route(GET("/functional/github/users/{id}/repos"), this::getUserGithubRepos));
    }

    private Mono<ServerResponse> getAllUsersGithubRepos(ServerRequest request) {
        return ServerResponse.ok().body(
                userRepository.findAll()
                        .flatMap(user -> githubClient.getUserRepos(user.getLogin())),
                GithubRepo.class);
    }

    private Mono<ServerResponse> getUserGithubRepos(ServerRequest request) {
        Mono<User> user = userRepository.findById(request.pathVariable("id")).cache();
        Mono<Boolean> isUserPresent = user.hasElement();
        return isUserPresent.flatMap(present -> {
           if (present) {
               return ServerResponse.ok().body(
                       user.flatMapMany(u -> githubClient.getUserRepos(u.getLogin())), GithubRepo.class);
           } else {
               return ServerResponse.notFound().build();
           }
        });
    }
}
