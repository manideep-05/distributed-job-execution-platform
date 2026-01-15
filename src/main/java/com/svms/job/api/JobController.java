package com.svms.job.api;

import com.svms.job.domain.JobDefinition;
import com.svms.job.domain.JobStatus;
import com.svms.job.repository.JobDefinitionRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobDefinitionRepository repository;

    public JobController(JobDefinitionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public JobDefinition createJob(@Valid @RequestBody CreateJobRequest request) {

        JobDefinition job = new JobDefinition();
        job.setName(request.getName());
        job.setJobType(request.getJobType());
        job.setScheduledAt(request.getScheduledAt());
        job.setPayload(request.getPayload());
        job.setMaxRetries(request.getMaxRetries());
        job.setStatus(JobStatus.ACTIVE);

        return repository.save(job);
    }
}
