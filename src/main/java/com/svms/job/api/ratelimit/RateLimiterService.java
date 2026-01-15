package com.svms.job.api.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimiterService {

    private final int maxRequests;
    private final long windowMillis;

    private final Map<String, RequestWindow> store = new ConcurrentHashMap<>();

    public RateLimiterService(
            @Value("${job.api.rate-limit.max-requests}") int maxRequests,
            @Value("${job.api.rate-limit.window-seconds}") int windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowSeconds * 1000L;
    }

    public boolean allow(String apiKey) {
        long now = System.currentTimeMillis();

        RequestWindow window = store.computeIfAbsent(
                apiKey,
                k -> new RequestWindow(now));

        synchronized (window) {
            if (now - window.startTime > windowMillis) {
                window.startTime = now;
                window.counter.set(0);
            }
            return window.counter.incrementAndGet() <= maxRequests;
        }
    }

    private static class RequestWindow {
        volatile long startTime;
        AtomicInteger counter = new AtomicInteger(0);

        RequestWindow(long startTime) {
            this.startTime = startTime;
        }
    }
}
