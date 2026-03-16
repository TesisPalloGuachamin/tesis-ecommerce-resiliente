package com.tesis.ecommerce.checkoutservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestedEvent {

    private UUID eventId;
    private UUID userId;
    private BigDecimal totalAmount;
    private List<CartItemDto> items;
    private Long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemDto {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }

}

