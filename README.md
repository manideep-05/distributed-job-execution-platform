# Distributed Job Execution Platform

A fault-tolerant backend platform for executing background jobs reliably using multiple workers, safe retries, idempotent execution, and dead-letter handling.

---

## Problem Statement

Modern backend systems rely heavily on background jobs for tasks such as:

- Sending emails and notifications
- Retrying failed payments
- Generating reports
- Synchronizing data between systems

Traditional cron-based scheduling is insufficient in distributed systems because it does not provide guarantees around:

- Duplicate execution
- Safe retries
- Crash recovery
- Execution visibility

This project focuses on solving these reliability problems rather than simple time-based scheduling.

---

## Why Not Cron?

Cron jobs:

- Are time-based, not state-based
- Cannot prevent overlapping executions
- Do not support safe retries
- Provide no execution history or failure visibility
- Break down in multi-node or containerized environments

This platform treats failures as first-class concerns and provides controlled execution semantics.

---

## High-Level Architecture

API → Job Definition → Scheduler → Job Execution → Worker → Job Handler

API -> Retry / DLQ

- **Scheduler** decides _when_ a job should run
- **Workers** compete to execute jobs safely
- **Job Handlers** encapsulate business logic
- **Database** acts as the source of truth for execution state

---

## Core Features

- Distributed execution with multiple workers
- Exactly-once execution per job attempt using database locking
- Retry with exponential backoff
- Idempotent job execution
- Dead Letter Queue (DLQ) for unrecoverable failures
- Persistent execution history for audit and debugging
- Extensible job types using Open/Closed Principle

---

## Non-Goals

- UI or dashboards
- Complex DAG workflows
- Message brokers (Kafka / RabbitMQ)
- Distributed transactions
- Cloud-specific scheduling services

---

## Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- JPA / Hibernate

---

## API Security

Job creation APIs are protected using API-key authentication and per-key rate limiting to prevent abuse.

## Failure Handling

Jobs are retried with exponential backoff. After retry exhaustion, executions are moved to a Dead Letter Queue for manual investigation.

## Trade-offs

- In-memory rate limiting chosen for MVP
- Database used as source of truth instead of message broker

---

## Status

🚧 Work in progress — currently implementing the core execution engine and reliability features.
