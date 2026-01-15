package com.svms.job.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BackoffCalculator {

    /**
     * Exponential backoff:
     * attempt=1 → 10s
     * attempt=2 → 20s
     * attempt=3 → 40s
     */
    public LocalDateTime calculateNextRun(int attempt) {
        long delaySeconds = (long) Math.pow(2, attempt - 1) * 10;
        return LocalDateTime.now().plusSeconds(delaySeconds);
    }
}
