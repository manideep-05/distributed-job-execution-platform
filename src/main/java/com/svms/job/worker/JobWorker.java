package com.svms.job.worker;

import com.svms.job.domain.ExecutionStatus;
import com.svms.job.domain.JobDefinition;
import com.svms.job.domain.JobExecution;
import com.svms.job.handler.JobHandler;
import com.svms.job.handler.JobHandlerRegistry;
import com.svms.job.repository.JobDefinitionRepository;
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
    private final JobDefinitionRepository jobDefinitionRepository;
    private final JobHandlerRegistry handlerRegistry;

    public JobWorker(JobExecutionRepository jobExecutionRepository, JobDefinitionRepository jobDefinitionRepository,
            JobHandlerRegistry handlerRegistry) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.handlerRegistry = handlerRegistry;
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
        JobDefinition job = jobDefinitionRepository
                .findById(execution.getJobId())
                .orElseThrow();

        try {
            // Mark RUNNING
            execution.setStatus(ExecutionStatus.RUNNING);
            execution.setStartedAt(LocalDateTime.now());

            // ---- DUMMY JOB EXECUTION ----
            JobHandler handler = handlerRegistry.getHandler(job.getJobType());
            handler.execute(job.getPayload());
            Thread.sleep(2000); // simulate work

            // Mark COMPLETED
            execution.setStatus(ExecutionStatus.COMPLETED);

        } catch (Exception e) {
            // execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            int nextAttempt = execution.getAttempt() + 1;

            if (nextAttempt <= job.getMaxRetries()) {
                execution.setAttempt(nextAttempt);
                execution.setStatus(ExecutionStatus.SCHEDULED);
                execution.setNextRunAt(
                        backoffCalculator.calculateNextRun(nextAttempt));
            } else {
                execution.setStatus(ExecutionStatus.DEAD);
            }

        } finally {
            execution.setFinishedAt(LocalDateTime.now());
            jobExecutionRepository.save(execution);
        }
    }
}
