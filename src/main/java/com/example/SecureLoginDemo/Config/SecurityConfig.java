package com.example.SecureLoginDemo.Config;

import com.example.SecureLoginDemo.security.GoogleLoginSuccessHandler;
import com.example.SecureLoginDemo.security.JwtAuthenticationFilter;
import com.example.SecureLoginDemo.security.RateLimitFilter;
import com.example.SecureLoginDemo.service.Google0idcUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
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
    private final Google0idcUserService google0idcUserService;
    private final GoogleLoginSuccessHandler googleLoginSuccessHandler;


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,RateLimitFilter rateLimitFilter,
                          Google0idcUserService google0idcUserService,GoogleLoginSuccessHandler googleLoginSuccessHandler){
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        this.rateLimitFilter=rateLimitFilter;
        this.google0idcUserService=google0idcUserService;
        this.googleLoginSuccessHandler=googleLoginSuccessHandler;
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
                .oauth2Login(oauth2-> oauth2.userInfoEndpoint(
                        userInfo -> userInfo
                        .oidcUserService(google0idcUserService))
                        .successHandler(googleLoginSuccessHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy
                        (SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(
                        auth->auth.requestMatchers("/api/auth/**","/oauth2/**","/login/**")
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
