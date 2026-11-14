package com.example.SecureLoginDemo.service;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class LoginAttemptService {

    private final ProxyManager<byte[]> proxyManager;

    public LoginAttemptService(ProxyManager<byte[]> proxyManager){
        this.proxyManager=proxyManager;
    }

    public BucketProxy getBucket(String email){
        BucketConfiguration configuration= BucketConfiguration.builder()
                .addLimit(limit ->limit
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(2)))
                .build();

        String key = "login_attempt:"+email.toLowerCase();

        return proxyManager.getProxy(
                key.getBytes(StandardCharsets.UTF_8),
                ()->configuration
        );
    }

    public boolean tryConsumeAttempt(String email){
        return getBucket(email).tryConsume(1);
    }

    public void refundSuccessfulAttempt(String email){
        getBucket(email).addTokens(1);
    }

}
