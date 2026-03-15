package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class CheckoutRepositoryAdapter implements CheckoutRepository {

    private final CheckoutRequestJpaRepository jpaRepository;

    public CheckoutRepositoryAdapter(CheckoutRequestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CheckoutRequest save(CheckoutRequest request) {
        request.setUpdatedAt(LocalDateTime.now());
        return jpaRepository.save(request);
    }

    @Override
    public Optional<CheckoutRequest> findByRequestId(UUID requestId) {
        return jpaRepository.findByRequestId(requestId);
    }

    @Override
    public Optional<CheckoutRequest> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}

