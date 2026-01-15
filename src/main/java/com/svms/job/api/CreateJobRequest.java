package com.svms.job.api;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.svms.job.domain.JobType;

@Component
@Data
public class CreateJobRequest {

    @NotBlank
    private String name;

    @NotNull
    private JobType jobType;

    @NotNull
    @Future
    private LocalDateTime scheduledAt;

    @NotNull
    @Size(max = 5000)
    private String payload;

    @Min(0)
    @Max(5)
    private int maxRetries = 3;

    // getters & setters
}
