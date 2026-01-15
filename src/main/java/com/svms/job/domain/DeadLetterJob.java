package com.svms.job.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "dead_letter_jobs")
public class DeadLetterJob {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID jobExecutionId;

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters & setters
}
