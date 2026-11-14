package com.example.SecureLoginDemo.service;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class RateLimiterService {

    private final ProxyManager<byte[]> proxyManager;

    public RateLimiterService(ProxyManager<byte[]> proxyManager){
        this.proxyManager=proxyManager;
    }

    public boolean isAllowed(String userEmail){

        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(limit-> limit
                        .capacity(5)
                        .refillGreedy(5, Duration.ofMinutes(1)))
                .build();

        String key = "rate_limit:"+userEmail;

        BucketProxy bucket= proxyManager.getProxy(
                key.getBytes(StandardCharsets.UTF_8),
                ()->configuration
        );

        return bucket.tryConsume(1);
    }
}
