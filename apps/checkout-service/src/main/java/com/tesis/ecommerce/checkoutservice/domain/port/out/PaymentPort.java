package com.tesis.ecommerce.checkoutservice.domain.port.out;

import java.util.UUID;

public interface PaymentPort {

    PaymentResult executePayment(UUID orderId, Double amount);

    class PaymentResult {
        public String transactionId;
        public boolean success;
        public String errorMessage;

        public PaymentResult(String transactionId, boolean success, String errorMessage) {
            this.transactionId = transactionId;
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }

}

