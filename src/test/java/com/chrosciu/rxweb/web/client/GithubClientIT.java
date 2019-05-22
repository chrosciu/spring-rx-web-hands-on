package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class GithubClientIT {
    @Autowired
    private GithubClient githubClient;

    @Test
    public void testGetUserRepos() {
        Iterable<GithubRepo> repos = githubClient.getUserRepos("chrosciu").toIterable();
        repos.forEach(r -> log.info(r.getName()));
    }

    @Test
    public void testGetUsersInRange() {
        Iterable<GithubUser> repos = githubClient.getUsersInRange(200, 240).toIterable();
        repos.forEach(u -> log.info("{} -> {}", u.getId(), u.getLogin()));
    }
}
