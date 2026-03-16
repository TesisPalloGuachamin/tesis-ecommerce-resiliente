package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedPaymentAdapterTest {

    private final SimulatedPaymentAdapter paymentAdapter = new SimulatedPaymentAdapter();

    @Test
    void testExecutePaymentReturnsResult() {
        // Act
        var result = paymentAdapter.executePayment(UUID.randomUUID(), new BigDecimal("100.00"));

        // Assert
        assertNotNull(result);
        assertNotNull(result.transactionId);
        assertTrue(result.success || result.errorMessage != null);
    }

    @Test
    void testExecutePaymentWithValidParameters() {
        // Act
        var result = paymentAdapter.executePayment(UUID.randomUUID(), new BigDecimal("50.00"));

        // Assert
        assertNotNull(result);
        assertNotNull(result.transactionId);
        if (!result.success) {
            assertNotNull(result.errorMessage);
        }
    }

}

