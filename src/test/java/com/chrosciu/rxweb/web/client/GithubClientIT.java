package com.chrosciu.rxweb.web.client;

import com.chrosciu.rxweb.model.GithubRepo;
import com.chrosciu.rxweb.model.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
@Disabled("For manual run only")
public class GithubClientIT {
    @Autowired
    private GithubClient githubClient;

    @Test
    public void testGetUserRepos() {
        Iterable<GithubRepo> repos = githubClient.getUserRepos("chrosciu").toIterable();
        repos.forEach(r -> log.info(r.getName()));
    }

    @Test
    public void testGetUserPublicBranchesCount() {
        Long branchesCount = githubClient.getUserNotProtectedBranchesCount("chrosciu").block();
        log.info("{}", branchesCount);
    }

    @Test
    public void testGetUsersInRange() {
        Iterable<GithubUser> repos = githubClient.getUsersInRange(200, 240).toIterable();
        repos.forEach(u -> log.info("{} -> {}", u.getId(), u.getLogin()));
    }
}
