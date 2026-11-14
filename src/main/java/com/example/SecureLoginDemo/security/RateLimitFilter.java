package com.example.SecureLoginDemo.security;

import com.example.SecureLoginDemo.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public RateLimitFilter(RateLimiterService rateLimiterService){
        this.rateLimiterService=rateLimiterService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return request.getServletPath().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()){
            String userEmail = authentication.getName();

           boolean allowed = rateLimiterService.isAllowed(userEmail);

            if (!allowed) {
                response.setStatus(429);
                response.setContentType("text/plain");
                response.getWriter().write(
                        "Too many requests. Please try again later."
                );
                return;
            }
        }

        filterChain.doFilter(request,response);

    }
}
