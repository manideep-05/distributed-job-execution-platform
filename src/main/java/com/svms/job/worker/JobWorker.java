package com.svms.job.worker;

import com.svms.job.domain.ExecutionStatus;
import com.svms.job.domain.JobExecution;
import com.svms.job.repository.JobExecutionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class JobWorker {

    private final JobExecutionRepository jobExecutionRepository;

    public JobWorker(JobExecutionRepository jobExecutionRepository) {
        this.jobExecutionRepository = jobExecutionRepository;
    }

    /**
     * Worker polls for scheduled executions and runs them safely.
     */
    @Scheduled(fixedDelay = 5000) // every 5 seconds
    @Transactional
    public void executeNextJob() {

        Optional<JobExecution> executionOpt = jobExecutionRepository.lockNextExecution(
                ExecutionStatus.SCHEDULED,
                LocalDateTime.now(),
                PageRequest.of(0, 1));

        if (executionOpt.isEmpty()) {
            return; // nothing to execute
        }

        JobExecution execution = executionOpt.get();

        try {
            // Mark RUNNING
            execution.setStatus(ExecutionStatus.RUNNING);
            execution.setStartedAt(LocalDateTime.now());

            // ---- DUMMY JOB EXECUTION ----
            System.out.println("Executing job execution: " + execution.getId());
            Thread.sleep(2000); // simulate work

            // Mark COMPLETED
            execution.setStatus(ExecutionStatus.COMPLETED);

        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
        } finally {
            execution.setFinishedAt(LocalDateTime.now());
            jobExecutionRepository.save(execution);
        }
    }
}
