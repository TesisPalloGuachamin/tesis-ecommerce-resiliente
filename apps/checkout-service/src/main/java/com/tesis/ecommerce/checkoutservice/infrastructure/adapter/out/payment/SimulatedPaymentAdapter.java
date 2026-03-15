package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import com.tesis.ecommerce.checkoutservice.domain.port.out.PaymentPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class SimulatedPaymentAdapter implements PaymentPort {

    private static final Random random = new Random();

    @Override
    public PaymentResult executePayment(UUID orderId, Double amount) {
        log.info("Executing simulated payment for order: {}, amount: {}", orderId, amount);

        // Simulate payment processing delay
        try {
            Thread.sleep(500 + random.nextInt(1500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(null, false, "Payment processing interrupted");
        }

        // Simulate 95% success rate for demo purposes
        boolean success = random.nextDouble() < 0.95;
        String transactionId = UUID.randomUUID().toString();

        if (success) {
            log.info("Simulated payment successful for order: {}, transaction: {}", orderId, transactionId);
            return new PaymentResult(transactionId, true, null);
        } else {
            String errorMessage = "Simulated payment failure - insufficient funds";
            log.warn("Simulated payment failed for order: {}, reason: {}", orderId, errorMessage);
            return new PaymentResult(transactionId, false, errorMessage);
        }
    }

}

