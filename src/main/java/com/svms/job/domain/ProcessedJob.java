package com.svms.job.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "processed_jobs")
public class ProcessedJob {

    @Id
    private String idempotencyKey;

    private LocalDateTime processedAt;
}
