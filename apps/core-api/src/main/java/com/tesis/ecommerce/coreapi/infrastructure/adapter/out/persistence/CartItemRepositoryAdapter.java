package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.domain.port.out.CartItemRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartItemRepositoryAdapter implements CartItemRepository {

    private final CartItemJpaRepository jpaRepository;

    public CartItemRepositoryAdapter(CartItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CartItem save(CartItem item) {
        return jpaRepository.save(item);
    }

    @Override
    public Optional<CartItem> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void delete(CartItem item) {
        jpaRepository.delete(item);
    }
}

