package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.OutboxEvent;
import java.util.UUID;

public interface EventPublisher {
    void publish(OutboxEvent event);
    void publishCheckoutRequested(UUID requestId, UUID userId, Double totalAmount);
}

