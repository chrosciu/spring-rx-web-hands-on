package com.chrosciu.rxweb.web.streaming;

import com.chrosciu.rxweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/streaming")
@RequiredArgsConstructor
public class UserStreamingController {
    private final UserRepository userRepository;

    //TODO: Delay as query param

    //TODO: Stream all users in 3 ways - as event stream, as SSE and as ND JSON


}
