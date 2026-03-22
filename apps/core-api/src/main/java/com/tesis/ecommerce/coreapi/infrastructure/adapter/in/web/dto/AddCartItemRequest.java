package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCartItemRequest {
    private UUID productId;
    private Integer quantity;
}

