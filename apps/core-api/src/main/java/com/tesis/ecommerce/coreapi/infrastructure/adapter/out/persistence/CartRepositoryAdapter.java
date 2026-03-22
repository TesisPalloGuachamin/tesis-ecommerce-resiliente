package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.port.out.CartRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository jpaRepository;

    public CartRepositoryAdapter(CartJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cart save(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
        return jpaRepository.save(cart);
    }

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}

