package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCheckoutRequest {

    @NotNull(message = "Cart ID cannot be null")
    private UUID cartId;
}
