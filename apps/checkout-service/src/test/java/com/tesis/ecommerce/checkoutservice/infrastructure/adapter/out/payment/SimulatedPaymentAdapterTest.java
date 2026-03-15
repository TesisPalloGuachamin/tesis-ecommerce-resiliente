package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.payment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedPaymentAdapterTest {

    private final SimulatedPaymentAdapter paymentAdapter = new SimulatedPaymentAdapter();

    @Test
    void testExecutePaymentReturnsResult() {
        // Act
        var result = paymentAdapter.executePayment(1L, 100.0);

        // Assert
        assertNotNull(result);
        assertNotNull(result.transactionId);
        assertTrue(result.success || result.errorMessage != null);
    }

    @Test
    void testExecutePaymentWithValidParameters() {
        // Act
        var result = paymentAdapter.executePayment(1L, 50.0);

        // Assert
        assertNotNull(result);
        assertNotNull(result.transactionId);
        if (!result.success) {
            assertNotNull(result.errorMessage);
        }
    }

}

