package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingDTO {
    private UUID id;
    private UUID sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String status;
    private String createdAt;
    private String updatedAt;
}
