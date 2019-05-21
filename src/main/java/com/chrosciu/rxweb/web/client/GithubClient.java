package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class GithubClient {
    private final WebClient webClient;

    public GithubClient(@Value("${github.url}") String githubUrl,
                        @Value("${github.username:#{null}}") String githubUsername,
                        @Value("${github.password:#{null}}") String githubPassword) {
        webClient = WebClient.builder()
                .baseUrl(githubUrl)
                .filter((request, next) -> {
                    if (githubUsername != null && !githubUsername.isEmpty() && githubPassword != null && !githubPassword.isEmpty()) {
                        next.filter(ExchangeFilterFunctions.basicAuthentication(githubUsername, githubPassword));
                    }
                    return next.exchange(request);
                })
                .build();
    }

    public Flux<GithubRepo> getUserRepos(String user) {
        return webClient.get()
                .uri("/users/{user}/repos", user)
                .exchange()
                .flatMapMany(response -> response.bodyToFlux(GithubRepo.class));
    }
}
