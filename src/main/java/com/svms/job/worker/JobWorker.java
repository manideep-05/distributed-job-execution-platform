package com.svms.job.worker;

import com.svms.job.domain.ExecutionStatus;
import com.svms.job.domain.JobDefinition;
import com.svms.job.domain.JobExecution;
import com.svms.job.handler.JobHandler;
import com.svms.job.handler.JobHandlerRegistry;
import com.svms.job.repository.JobDefinitionRepository;
import com.svms.job.repository.JobExecutionRepository;
import com.svms.job.service.BackoffCalculator;
import com.svms.job.service.DeadLetterService;
import com.svms.job.service.IdempotencyService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class JobWorker {

    private final JobExecutionRepository jobExecutionRepository;
    private final JobDefinitionRepository jobDefinitionRepository;
    private final JobHandlerRegistry handlerRegistry;
    private final BackoffCalculator backoffCalculator;
    private final IdempotencyService idempotencyService;
    private final DeadLetterService deadLetterService;

    public JobWorker(JobExecutionRepository jobExecutionRepository, JobDefinitionRepository jobDefinitionRepository,
            JobHandlerRegistry handlerRegistry, BackoffCalculator backoffCalculator,
            IdempotencyService idempotencyService, DeadLetterService deadLetterService) {
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.handlerRegistry = handlerRegistry;
        this.backoffCalculator = backoffCalculator;
        this.idempotencyService = idempotencyService;
        this.deadLetterService = deadLetterService;
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

            // Idempotency check
            String idempotencyKey = handler.extractIdempotencyKey(job.getPayload());

            if (idempotencyService.isAlreadyProcessed(idempotencyKey)) {
                execution.setStatus(ExecutionStatus.COMPLETED);
                execution.setFinishedAt(LocalDateTime.now());
                jobExecutionRepository.save(execution);
                return;
            }
            log.info("Worker picked execution {}", execution.getId());
            handler.execute(job.getPayload());
            idempotencyService.markProcessed(idempotencyKey);
            Thread.sleep(2000); // simulate work

            // Mark COMPLETED
            execution.setStatus(ExecutionStatus.COMPLETED);

        } catch (Exception e) {
            // execution.setStatus(ExecutionStatus.FAILED);
            log.error("Execution {} failed on attempt {}", execution.getId(), execution.getAttempt());
            execution.setErrorMessage(e.getMessage());
            int nextAttempt = execution.getAttempt() + 1;

            if (nextAttempt <= job.getMaxRetries()) {
                execution.setAttempt(nextAttempt);
                execution.setStatus(ExecutionStatus.SCHEDULED);
                execution.setNextRunAt(
                        backoffCalculator.calculateNextRun(nextAttempt));
            } else {
                execution.setStatus(ExecutionStatus.DEAD);
                deadLetterService.moveToDLQ(
                        execution.getId().toString(),
                        e.getMessage());
            }

        } finally {
            execution.setFinishedAt(LocalDateTime.now());
            jobExecutionRepository.save(execution);
        }
    }
}
