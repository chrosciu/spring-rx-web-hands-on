package com.chrosciu.rxweb.web.functional;

import com.chrosciu.rxweb.repository.UserRepository;
import com.chrosciu.rxweb.web.client.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class UserGithubRouter {
    private final UserRepository userRepository;
    private final GithubClient githubClient;

    @Bean
    RouterFunction<ServerResponse> userGithubRoutes() {
        return null;
    }

    //TODO Create following REST endpoint using functional endpoints
    //GET /functional/users/repos - should return all GitHub repositories of all users

}
