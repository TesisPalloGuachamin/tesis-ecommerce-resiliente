package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import com.tesis.ecommerce.checkoutservice.domain.port.out.PaymentPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class StripeSimulatedPaymentAdapter implements PaymentPort {

    private static final String TRANSACTION_PREFIX = "stripe_sim_";

    @Override
    public PaymentResult executePayment(UUID orderId, BigDecimal amount) {
        log.info("Executing Stripe simulated payment for order: {}, amount: {}", orderId, amount);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new PaymentResult(null, false, "Stripe simulated payment interrupted");
        }

        String transactionId = TRANSACTION_PREFIX + UUID.randomUUID();
        log.info("Stripe simulated payment approved for order: {}, transaction: {}", orderId, transactionId);
        return new PaymentResult(transactionId, true, null);
    }

}
