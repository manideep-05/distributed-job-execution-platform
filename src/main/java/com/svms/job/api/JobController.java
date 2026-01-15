package com.svms.job.api;

import com.svms.job.api.validation.JobRequestValidator;
import com.svms.job.domain.JobDefinition;
import com.svms.job.domain.JobStatus;
import com.svms.job.repository.JobDefinitionRepository;
import com.svms.job.service.ApiIdempotencyService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobDefinitionRepository repository;
    private final JobRequestValidator Validator;
    private final ApiIdempotencyService idempotencyService;

    public JobController(JobDefinitionRepository repository, JobRequestValidator Validator,
            ApiIdempotencyService idempotencyService) {
        this.repository = repository;
        this.Validator = Validator;
        this.idempotencyService = idempotencyService;
    }

    @PostMapping
    public ResponseEntity<JobDefinition> createJob(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateJobRequest request) {

        Validator.validate(request);

        if (idempotencyKey != null) {
            return idempotencyService
                    .getExistingJob(idempotencyKey)
                    .map(job -> ResponseEntity.ok(job))
                    .orElseGet(() -> createAndStoreJob(request, idempotencyKey));
        }

        // return repository.save(job);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(buildJob(request)));
    }

    private ResponseEntity<JobDefinition> createAndStoreJob(
            CreateJobRequest request,
            String key) {

        JobDefinition job = repository.save(buildJob(request));
        idempotencyService.storeKey(key, job);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(job);
    }

    private JobDefinition buildJob(CreateJobRequest request) {
        JobDefinition job = new JobDefinition();
        job.setName(request.getName());
        job.setJobType(request.getJobType());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());
        job.setMaxRetries(request.getMaxRetries());
        job.setStatus(JobStatus.ACTIVE);
        return job;
    }

}
