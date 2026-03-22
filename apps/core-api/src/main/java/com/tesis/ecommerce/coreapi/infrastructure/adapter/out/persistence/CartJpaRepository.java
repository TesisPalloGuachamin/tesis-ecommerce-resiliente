package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartJpaRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID userId);
}

