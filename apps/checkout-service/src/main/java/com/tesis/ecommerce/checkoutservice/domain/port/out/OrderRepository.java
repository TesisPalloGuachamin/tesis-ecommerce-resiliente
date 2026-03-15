package com.tesis.ecommerce.checkoutservice.domain.port.out;

import com.tesis.ecommerce.checkoutservice.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findByCheckoutRequestId(UUID checkoutRequestId);

}

