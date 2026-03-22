package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItem, UUID> {
}

