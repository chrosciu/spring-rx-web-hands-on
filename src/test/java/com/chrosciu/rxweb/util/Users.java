package com.chrosciu.rxweb.util;

import com.chrosciu.rxweb.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Users {
    public static final User JANUSZ = User.builder().id("1").firstName("Janusz").lastName("Cebulak").githubName("chrosciu").build();
    public static final User MIREK = User.builder().id("2").firstName("Mirek").lastName("Handlarz").githubName("octocat").build();
}
