package com.svms.job.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class ExecutionMetricsService {

    private final AtomicLong success = new AtomicLong();
    private final AtomicLong failure = new AtomicLong();

    public void recordSuccess() {
        success.incrementAndGet();
    }

    public void recordFailure() {
        failure.incrementAndGet();
    }

    public long successCount() {
        return success.get();
    }

    public long failureCount() {
        return failure.get();
    }
}
