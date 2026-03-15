package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
            .id(product.getId())
            .sku(product.getSku())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .stock(product.getStock())
            .active(product.getActive())
            .build();
    }

    public Product toDomain(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        return Product.builder()
            .id(dto.getId())
            .sku(dto.getSku())
            .name(dto.getName())
            .description(dto.getDescription())
            .price(dto.getPrice())
            .stock(dto.getStock())
            .active(dto.getActive())
            .build();
    }
}

