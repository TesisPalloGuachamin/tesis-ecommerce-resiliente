package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.CartItem;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository {
    CartItem save(CartItem item);
    Optional<CartItem> findById(UUID id);
    void delete(CartItem item);
}

