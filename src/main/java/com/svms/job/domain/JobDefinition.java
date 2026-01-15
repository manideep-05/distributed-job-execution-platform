package com.svms.job.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "job_definitions")
public class JobDefinition {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String jobType; // EMAIL, PAYMENT_RETRY, DATA_SYNC

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.ACTIVE;

    private LocalDateTime scheduledAt;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private int maxRetries = 3;

    private LocalDateTime createdAt = LocalDateTime.now();
}
