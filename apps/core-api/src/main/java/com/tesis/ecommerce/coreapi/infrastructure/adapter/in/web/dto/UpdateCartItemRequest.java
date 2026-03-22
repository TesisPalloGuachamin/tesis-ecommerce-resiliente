package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemRequest {
    private Integer quantity;
}

