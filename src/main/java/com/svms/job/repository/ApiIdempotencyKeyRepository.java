package com.svms.job.repository;

import com.svms.job.domain.ApiIdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiIdempotencyKeyRepository
        extends JpaRepository<ApiIdempotencyKey, String> {
}
