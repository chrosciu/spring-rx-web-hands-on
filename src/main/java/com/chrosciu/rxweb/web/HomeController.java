package com.chrosciu.rxweb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class HomeController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
