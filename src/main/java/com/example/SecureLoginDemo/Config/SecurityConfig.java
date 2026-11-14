package com.example.SecureLoginDemo.Config;

import com.example.SecureLoginDemo.security.JwtAuthenticationFilter;
import com.example.SecureLoginDemo.security.RateLimitFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RateLimitFilter rateLimitFilter;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,RateLimitFilter rateLimitFilter){
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        this.rateLimitFilter=rateLimitFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(jwtAuthenticationFilter);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitRegistration(){

        FilterRegistrationBean<RateLimitFilter> registration =
                new FilterRegistrationBean<>(rateLimitFilter);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy
                        (SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth->auth.requestMatchers("/api/auth/**")
                                .permitAll()
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                                .accessDeniedHandler((request,response,accessDeniedException)
                ->response.setStatus(HttpServletResponse.SC_FORBIDDEN)));

        return http.build();
    }
}
