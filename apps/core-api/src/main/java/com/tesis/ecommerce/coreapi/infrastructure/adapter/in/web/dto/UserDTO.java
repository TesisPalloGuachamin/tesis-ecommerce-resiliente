package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String email;
    private String name;
    private Boolean enabled;
}

