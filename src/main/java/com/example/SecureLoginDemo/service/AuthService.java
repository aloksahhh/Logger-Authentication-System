package com.example.SecureLoginDemo.service;

import com.example.SecureLoginDemo.dto.LoginResponse;
import com.example.SecureLoginDemo.dto.RegisterRequest;
import com.example.SecureLoginDemo.entity.User;
import com.example.SecureLoginDemo.enumtype.Role;
import com.example.SecureLoginDemo.exception.TooManyLoginAttemptException;
import com.example.SecureLoginDemo.repository.UserRepository;
import com.example.SecureLoginDemo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final LoginAttemptService loginAttemptService;


    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService,LoginAttemptService loginAttemptService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService = jwtService;
        this.loginAttemptService=loginAttemptService;
    }

    public String register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        User user=new User();

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);

        userRepository.save(user);

        return "User registered successfully...";
    }

    public LoginResponse login(String email, String password){

        if(!loginAttemptService.tryConsumeAttempt(email)){
            throw new TooManyLoginAttemptException("Too many login attempts.Please try again later.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Invalid username or password..."));

        if(!passwordEncoder.matches(password,user.getPassword())){
             throw new RuntimeException("Invalid username or password...");
        }

        loginAttemptService.refundSuccessfulAttempt(email);

        String token= jwtService.generateToken(user.getEmail(),user.getRole().name());

        return new LoginResponse(token);
    }
}
