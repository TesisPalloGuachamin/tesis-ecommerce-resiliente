package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.messaging.dto;

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
    private Long timestamp;

}

