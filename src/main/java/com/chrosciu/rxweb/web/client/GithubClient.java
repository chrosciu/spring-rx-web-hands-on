package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubBranch;
import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
@Slf4j
public class GithubClient {
    private final WebClient webClient;

    public GithubClient(@Value("${github.url}") String githubUrl,
                        @Value("${github.token:#{null}}") String githubToken,
                        @Value("${webclient.wiretap.enabled:#{null}}") Boolean wiretapEnabled) {
        HttpClient httpClient = HttpClient.create();
        if (Boolean.TRUE.equals(wiretapEnabled)) {
                httpClient = httpClient.wiretap(
                        this.getClass().getCanonicalName(), LogLevel.TRACE, AdvancedByteBufFormat.TEXTUAL);
        }
        ClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);
        webClient = WebClient.builder()
                .clientConnector(conn)
                .baseUrl(githubUrl)
                .defaultHeaders(httpHeaders -> addAuthenticationHeaders(httpHeaders, githubToken))
                .build();
    }

    private void addAuthenticationHeaders(HttpHeaders httpHeaders, String githubToken) {
        if (githubToken != null && !githubToken.isEmpty()) {
            log.info("Created WebClient authentication filter with token");
            httpHeaders.set("Authorization", "token " + githubToken);
        } else {
            log.info("WebClient without authentication headers");
        }
    }

    public Flux<GithubRepo> getUserRepos(String user) {
        return webClient.get()
                .uri("/users/{user}/repos", user)
                .exchangeToFlux(response -> {
                    if (response.statusCode().isError()) {
                        return response.createException().flatMapMany(Flux::error);
                    } else {
                        return response.bodyToFlux(GithubRepo.class);
                    }
                });
    }

    public Mono<Long> getUserNotProtectedBranchesCount(String user) {
        return getUserRepos(user).flatMap(repo -> getRepoPublicBranches(user, repo.getName())).count();
    }

    private Flux<GithubBranch> getRepoPublicBranches(String user, String repo) {
        return webClient.get()
                .uri("/repos/{user}/{repo}/branches", user, repo)
                .exchangeToFlux(response -> response
                        .bodyToFlux(GithubBranch.class))
                .filter(gb -> !Boolean.TRUE.equals(gb.getProtect()));
    }

    public Flux<GithubUser> getUsersInRange(long sinceId, long toId) {
        Flux<GithubUser> currentPageUntilTo = getPageOfUsers(sinceId).takeWhile(u -> u.getId() <= toId).cache();
        Mono<Long> lastUserId = currentPageUntilTo.last().map(GithubUser::getId);
        Flux<GithubUser> nextPage = lastUserId.flatMapMany(l -> l >= toId ? Flux.empty() : getUsersInRange(l, toId));
        return currentPageUntilTo.concatWith(nextPage);
    }

    private Flux<GithubUser> getPageOfUsers(long sinceId) {
        return webClient.get()
                .uri("/users?since={sinceId}", sinceId)
                .exchangeToFlux(response -> response.bodyToFlux(GithubUser.class));
    }


}
