package com.svms.job.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "api_idempotency_keys")
public class ApiIdempotencyKey {

    @Id
    private String key;

    private UUID jobId;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
}
