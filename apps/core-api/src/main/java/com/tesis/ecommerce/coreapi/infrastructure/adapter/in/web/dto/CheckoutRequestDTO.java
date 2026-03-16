package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO {
    private UUID requestId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
}

