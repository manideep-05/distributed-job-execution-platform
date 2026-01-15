package com.svms.job.repository;

import com.svms.job.domain.ExecutionStatus;
import com.svms.job.domain.JobExecution;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface JobExecutionRepository
        extends JpaRepository<JobExecution, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT e FROM JobExecution e
                WHERE e.status = :status
                  AND e.nextRunAt <= :now
                ORDER BY e.nextRunAt
            """)
    Optional<JobExecution> lockNextExecution(
            ExecutionStatus status,
            LocalDateTime now,
            Pageable pageable);
}
