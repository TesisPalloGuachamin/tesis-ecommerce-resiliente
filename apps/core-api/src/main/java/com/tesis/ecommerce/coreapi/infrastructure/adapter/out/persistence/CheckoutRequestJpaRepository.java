package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckoutRequestJpaRepository extends JpaRepository<CheckoutRequest, UUID> {
    Optional<CheckoutRequest> findByRequestId(UUID requestId);
}

