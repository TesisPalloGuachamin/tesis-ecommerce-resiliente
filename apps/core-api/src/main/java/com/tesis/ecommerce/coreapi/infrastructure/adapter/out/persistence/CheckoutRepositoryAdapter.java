package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public Optional<CheckoutRequest> findByRequestId(String requestId) {
        return jpaRepository.findByRequestId(requestId);
    }

    @Override
    public Optional<CheckoutRequest> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

