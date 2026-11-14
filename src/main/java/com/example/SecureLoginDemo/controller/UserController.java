package com.example.SecureLoginDemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/user/profile")
    public String profiles(){
        return "You are authenticated!";
    }
}
