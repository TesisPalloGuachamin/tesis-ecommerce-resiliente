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
public class CheckoutFailedEvent {

    private UUID eventId;
    private UUID checkoutRequestId;
    private UUID userId;
    private String reason;
    private Long timestamp;

}

