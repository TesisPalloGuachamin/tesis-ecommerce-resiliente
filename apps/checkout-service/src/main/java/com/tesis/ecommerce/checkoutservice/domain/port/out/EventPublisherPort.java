package com.tesis.ecommerce.checkoutservice.domain.port.out;

import java.util.UUID;

public interface EventPublisherPort {

    void publishCheckoutAccepted(UUID checkoutRequestId, UUID orderId);

    void publishPaymentProcessed(UUID orderId, String transactionId, boolean success);

    void publishCheckoutCompleted(UUID checkoutRequestId, UUID orderId);

    void publishCheckoutFailed(UUID checkoutRequestId, String reason);

}

