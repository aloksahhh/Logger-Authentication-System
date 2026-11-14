package com.example.SecureLoginDemo.controller;

import com.example.SecureLoginDemo.dto.LoginRequest;
import com.example.SecureLoginDemo.dto.LoginResponse;
import com.example.SecureLoginDemo.dto.RegisterRequest;
import com.example.SecureLoginDemo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody  RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid
            @RequestBody LoginRequest request){

        return authService.login(request.getEmail(),request.getPassword());
    }
}
