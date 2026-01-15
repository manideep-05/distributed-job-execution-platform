package com.svms.job.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
@Data
public class CreateJobRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String jobType;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotNull
    private String payload;

    private int maxRetries = 3;

    // getters & setters
}
