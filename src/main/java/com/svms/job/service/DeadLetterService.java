package com.svms.job.service;

import com.svms.job.domain.DeadLetterJob;
import com.svms.job.repository.DeadLetterJobRepository;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterService {

    private final DeadLetterJobRepository repository;

    public DeadLetterService(DeadLetterJobRepository repository) {
        this.repository = repository;
    }

    public void moveToDLQ(String executionId, String reason) {
        DeadLetterJob dlq = new DeadLetterJob();
        dlq.setJobExecutionId(java.util.UUID.fromString(executionId));
        dlq.setReason(reason);
        repository.save(dlq);
    }
}
