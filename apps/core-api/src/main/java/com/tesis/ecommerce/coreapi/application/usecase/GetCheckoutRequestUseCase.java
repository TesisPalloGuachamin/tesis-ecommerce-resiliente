package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CheckoutRequestDTO;
import com.tesis.ecommerce.coreapi.application.mapper.CheckoutMapper;
import org.springframework.stereotype.Service;

@Service
public class GetCheckoutRequestUseCase {

    private final CheckoutRepository checkoutRepository;
    private final CheckoutMapper checkoutMapper;

    public GetCheckoutRequestUseCase(CheckoutRepository checkoutRepository, CheckoutMapper checkoutMapper) {
        this.checkoutRepository = checkoutRepository;
        this.checkoutMapper = checkoutMapper;
    }

    public CheckoutRequestDTO execute(String requestId) {
        CheckoutRequest request = checkoutRepository.findByRequestId(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Checkout request not found"));
        return checkoutMapper.toDTO(request);
    }
}

