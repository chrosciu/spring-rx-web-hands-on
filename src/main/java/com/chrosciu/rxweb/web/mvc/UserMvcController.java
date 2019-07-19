package com.chrosciu.rxweb.web.mvc;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mvc")
@RequiredArgsConstructor
public class UserMvcController {
    private final UserRepository userRepository;

    //TODO Create following REST endpoints using annotated controller methods.
    // Both request body and response body should use reactive types only
    // 1) GET /mvc/users - should return all users
    // 2) GET /mvc/users/{id} - should return user with given id
    // 3) POST /mvc/users - should save user passed as body and return saved user
    // 4) DELETE /mvc/users/{id} - should delete user with given id
}

