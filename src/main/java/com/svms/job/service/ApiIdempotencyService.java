package com.svms.job.service;

import com.svms.job.domain.ApiIdempotencyKey;
import com.svms.job.domain.JobDefinition;
import com.svms.job.repository.ApiIdempotencyKeyRepository;
import com.svms.job.repository.JobDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ApiIdempotencyService {

    private final ApiIdempotencyKeyRepository repository;
    private final JobDefinitionRepository jobRepository;

    public ApiIdempotencyService(
            ApiIdempotencyKeyRepository repository,
            JobDefinitionRepository jobRepository) {
        this.repository = repository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public Optional<JobDefinition> getExistingJob(String key) {
        return repository.findById(key)
                .flatMap(k -> jobRepository.findById(k.getJobId()));
    }

    @Transactional
    public void storeKey(String key, JobDefinition job) {
        ApiIdempotencyKey entry = new ApiIdempotencyKey();
        entry.setKey(key);
        entry.setJobId(job.getId());
        repository.save(entry);
    }
}
