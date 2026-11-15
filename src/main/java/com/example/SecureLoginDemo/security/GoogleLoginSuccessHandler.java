package com.example.SecureLoginDemo.security;

import com.example.SecureLoginDemo.entity.User;
import com.example.SecureLoginDemo.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public GoogleLoginSuccessHandler(UserRepository userRepository,JwtService jwtService,ObjectMapper objectMapper){
        this.userRepository=userRepository;
        this.jwtService=jwtService;
        this.objectMapper=objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String email = oidcUser.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("User not found in database"));

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> responseBody = Map.of(
                "message", "Google login successful",
                "email", user.getEmail(),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "tokenType", "Bearer",
                "token", token
        );

        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
