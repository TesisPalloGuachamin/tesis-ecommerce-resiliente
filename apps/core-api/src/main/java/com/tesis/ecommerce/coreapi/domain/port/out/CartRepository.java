package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findByUserId(UUID userId);
    Optional<Cart> findById(UUID id);
}

