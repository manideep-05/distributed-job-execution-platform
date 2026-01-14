package com.svms.job.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_executions")
public class JobExecution {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID jobId;

    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    private int attempt;

    private LocalDateTime nextRunAt;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String errorMessage;
}
