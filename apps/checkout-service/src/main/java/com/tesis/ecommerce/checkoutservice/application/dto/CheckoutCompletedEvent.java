package com.tesis.ecommerce.checkoutservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutCompletedEvent {

    private UUID eventId;
    private UUID checkoutRequestId;
    private UUID orderId;
    private UUID userId;
    private Double totalAmount;
    private String status;
    private Long timestamp;

}

