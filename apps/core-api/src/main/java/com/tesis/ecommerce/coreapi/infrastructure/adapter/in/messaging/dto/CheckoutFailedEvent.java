package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutFailedEvent {

    private UUID eventId;
    private UUID checkoutRequestId;
    private String reason;
    private Long timestamp;

}

