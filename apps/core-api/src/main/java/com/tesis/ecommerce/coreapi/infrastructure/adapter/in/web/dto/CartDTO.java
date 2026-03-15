package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> items = new ArrayList<>();
    private Double total;
}

