package com.codehunters.healthcare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    @ResponseBody
    public String home(java.security.Principal principal) {
        if (principal == null) {
            return "You are browsing as a guest. Not logged in.";
        }
        return "Logged in as: " + principal.getName();
    }

    
}