package com.svms.job.api.validation;

import com.svms.job.api.CreateJobRequest;
import org.springframework.stereotype.Component;

@Component
public class JobRequestValidator {

    public void validate(CreateJobRequest request) {

        if (request.getScheduledAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("scheduledAt must be in the future");
        }

        if (request.getPayload() == null || request.getPayload().isBlank()) {
            throw new IllegalArgumentException("payload cannot be empty");
        }
    }
}
