package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import com.tesis.ecommerce.checkoutservice.domain.port.out.PaymentPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class StripeSimulatedPaymentAdapter implements PaymentPort {

    private static final String TRANSACTION_PREFIX = "stripe_sim_";
    private static final String DECLINED_MESSAGE = "simulated decline";

    @Value("${payment.simulator.decline.enabled:false}")
    private boolean declineEnabled;

    @Value("${payment.simulator.decline.amount:29.99}")
    private BigDecimal declineAmount;

    @Override
    public PaymentResult executePayment(UUID orderId, BigDecimal amount) {
        log.info("Executing Stripe simulated payment for order: {}, amount: {}", orderId, amount);

        if (declineEnabled && amount != null && declineAmount != null
                && amount.compareTo(declineAmount) == 0) {
            log.warn("Stripe simulated payment declined for order: {}, amount: {}, reason: {}",
                    orderId, amount, DECLINED_MESSAGE);
            return new PaymentResult(null, false, DECLINED_MESSAGE);
        }

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
