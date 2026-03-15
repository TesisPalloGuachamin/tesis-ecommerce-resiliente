package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;

import java.util.Optional;

public interface CheckoutRepository {
    CheckoutRequest save(CheckoutRequest request);
    Optional<CheckoutRequest> findByRequestId(String requestId);
    Optional<CheckoutRequest> findById(Long id);
}

