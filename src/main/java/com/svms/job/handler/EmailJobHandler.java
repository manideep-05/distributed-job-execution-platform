package com.svms.job.handler;

import org.springframework.stereotype.Component;

@Component
public class EmailJobHandler implements JobHandler {

    @Override
    public void execute(String payload) {
        System.out.println("Sending email with payload: " + payload);
    }

    @Override
    public String getJobType() {
        return "EMAIL";
    }

    @Override
    public String extractIdempotencyKey(String payload) {
        return payload; // paymentId
    }

}
