package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.checkoutservice.domain.model.Order;
import com.tesis.ecommerce.checkoutservice.domain.port.out.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepository {

    @Override
    Optional<Order> findByCheckoutRequestId(UUID checkoutRequestId);

}

