package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CartDTO;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CartItemDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    private final ProductMapper productMapper;

    public CartMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public CartDTO toDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartDTO.builder()
            .id(cart.getId())
            .userId(cart.getUser().getId())
            .items(cart.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()))
            .total(cart.getTotal())
            .build();
    }

    public CartItemDTO toItemDTO(CartItem item) {
        if (item == null) {
            return null;
        }
        return CartItemDTO.builder()
            .id(item.getId())
            .productId(item.getProduct().getId())
            .product(productMapper.toDTO(item.getProduct()))
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .total(item.getTotal())
            .build();
    }
}

