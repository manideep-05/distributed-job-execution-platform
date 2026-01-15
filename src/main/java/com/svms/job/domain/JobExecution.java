package com.svms.job.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "job_executions")
public class JobExecution {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID jobId;

    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @Column(unique = true)
    private String idempotencyKey;

    private int attempt;

    private LocalDateTime nextRunAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String errorMessage;
}
