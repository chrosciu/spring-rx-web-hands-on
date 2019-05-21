package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
public class GithubClientTest {
    private GithubClient githubClient;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    @Before
    public void setup() {
        githubClient = new GithubClient(String.format("http://localhost:%d", wireMockRule.port()), null, null);
    }

    @Test
    public void testGetRepos() throws Exception {
        //given
        String user = "johndoe";
        GithubRepo githubRepo1 = GithubRepo.builder().id(1L).name("test").build();
        GithubRepo githubRepo2 = GithubRepo.builder().id(2L).name("foobar").build();
        List<GithubRepo> githubRepos = Arrays.asList(githubRepo1, githubRepo2);
        String githubRepoJson = new ObjectMapper().writeValueAsString(githubRepos);
        stubFor(get(urlEqualTo(String.format("/users/%s/repos", user)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(githubRepoJson)));
        //when
        Flux<GithubRepo> repos = githubClient.getUserRepos(user);
        //then
        StepVerifier.create(repos)
                .expectNext(githubRepo1, githubRepo2)
                .verifyComplete();
    }




}
