package com.svms.job.handler;

public interface JobHandler {

    /**
     * Execute business logic for a job.
     * 
     * @param payload job-specific data
     */
    void execute(String payload) throws Exception;

    /**
     * @return job type this handler supports (EMAIL, REPORT, etc.)
     */
    String getJobType();
}
