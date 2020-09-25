package com.chrosciu.rxweb.data;

import com.chrosciu.rxweb.model.User;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestUsers {
    public static final User CHROSCIU = User.builder().id("1").login("chrosciu").build();
    public static final User CHROSCIU_UNSAVED = CHROSCIU.withId(null);
    public static final User OCTOCAT = User.builder().id("2").login("octocat").build();
}
