package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
}

