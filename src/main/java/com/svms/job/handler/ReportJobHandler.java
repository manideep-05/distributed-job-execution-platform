package com.svms.job.handler;

import org.springframework.stereotype.Component;

@Component
public class ReportJobHandler implements JobHandler {

    @Override
    public void execute(String payload) {
        System.out.println("Generating report with payload: " + payload);
    }

    @Override
    public String getJobType() {
        return "REPORT";
    }
}