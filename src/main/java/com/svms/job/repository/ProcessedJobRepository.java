package com.svms.job.repository;

import com.svms.job.domain.ProcessedJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedJobRepository
        extends JpaRepository<ProcessedJob, String> {
}
