package com.svms.job.repository;

import com.svms.job.domain.DeadLetterJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeadLetterJobRepository
        extends JpaRepository<DeadLetterJob, UUID> {
}
