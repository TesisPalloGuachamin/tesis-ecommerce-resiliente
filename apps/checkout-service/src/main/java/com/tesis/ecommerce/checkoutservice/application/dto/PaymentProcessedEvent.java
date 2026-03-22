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
public class PaymentProcessedEvent {

    private UUID eventId;
    private UUID orderId;
    private String transactionId;
    private boolean success;
    private Double amount;
    private Long timestamp;

}

