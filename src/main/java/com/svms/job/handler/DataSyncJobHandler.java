package com.svms.job.handler;

import org.springframework.stereotype.Component;

@Component
public class DataSyncJobHandler implements JobHandler {

    @Override
    public void execute(String payload) {
        System.out.println("Syncing data with payload: " + payload);
    }

    @Override
    public String getJobType() {
        return "DATA_SYNC";
    }
}