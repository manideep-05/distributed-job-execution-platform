package com.svms.job.handler;

import org.springframework.stereotype.Component;

@Component
public class PaymentRetryJobHandler implements JobHandler {

    @Override
    public void execute(String payload) {
        System.out.println("Retrying payment: " + payload);
    }

    @Override
    public String getJobType() {
        return "PAYMENT_RETRY";
    }

    @Override
    public String extractIdempotencyKey(String payload) {
        return payload; // paymentId
    }
}
