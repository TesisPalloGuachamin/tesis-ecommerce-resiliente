package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.OutboxEvent;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EventPublisher {
    void publish(OutboxEvent event);
    void publishCheckoutRequested(UUID requestId, UUID userId, BigDecimal totalAmount, List<CartItem> items);
}

