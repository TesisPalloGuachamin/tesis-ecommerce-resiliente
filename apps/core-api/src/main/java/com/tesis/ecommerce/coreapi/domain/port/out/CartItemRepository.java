package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.CartItem;

import java.util.Optional;

public interface CartItemRepository {
    CartItem save(CartItem item);
    Optional<CartItem> findById(Long id);
    void delete(CartItem item);
}

