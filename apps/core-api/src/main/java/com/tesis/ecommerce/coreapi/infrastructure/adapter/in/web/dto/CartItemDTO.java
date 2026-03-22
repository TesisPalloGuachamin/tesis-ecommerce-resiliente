package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}

