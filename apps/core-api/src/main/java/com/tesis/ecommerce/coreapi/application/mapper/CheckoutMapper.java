package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CheckoutRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class CheckoutMapper {

    public CheckoutRequestDTO toDTO(CheckoutRequest request) {
        if (request == null) {
            return null;
        }
        return CheckoutRequestDTO.builder()
            .requestId(request.getRequestId())
            .userId(request.getUser().getId())
            .totalAmount(request.getTotalAmount())
            .status(request.getStatus().toString())
            .build();
    }
}

