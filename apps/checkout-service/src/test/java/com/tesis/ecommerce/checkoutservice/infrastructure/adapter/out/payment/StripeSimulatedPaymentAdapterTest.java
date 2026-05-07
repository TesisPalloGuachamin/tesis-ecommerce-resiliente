package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StripeSimulatedPaymentAdapterTest {

    private final StripeSimulatedPaymentAdapter paymentAdapter = new StripeSimulatedPaymentAdapter();

    @Test
    void testExecutePaymentReturnsApprovedStripeSimulatedResult() {
        var result = paymentAdapter.executePayment(UUID.randomUUID(), new BigDecimal("100.00"));

        assertNotNull(result);
        assertNotNull(result.transactionId);
        assertTrue(result.transactionId.startsWith("stripe_sim_"));
        assertTrue(result.success);
        assertNull(result.errorMessage);
    }

    @Test
    void testExecutePaymentWithValidParameters() {
        var result = paymentAdapter.executePayment(UUID.randomUUID(), new BigDecimal("50.00"));

        assertNotNull(result);
        assertNotNull(result.transactionId);
        assertTrue(result.success);
    }

}
