package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRegisterRequest {
    private String email;
    private String password;
    private String name;
}

