package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;

import java.util.Optional;
import java.util.UUID;

public interface CheckoutRepository {
    CheckoutRequest save(CheckoutRequest request);
    Optional<CheckoutRequest> findByRequestId(UUID requestId);
    Optional<CheckoutRequest> findById(UUID id);
}

