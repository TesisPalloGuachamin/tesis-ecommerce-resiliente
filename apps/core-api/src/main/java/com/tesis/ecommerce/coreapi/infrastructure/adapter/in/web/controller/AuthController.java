package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.application.usecase.RegisterUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.LoginUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.GetCurrentUserUseCase;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthRegisterRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthLoginRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AuthResponse;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase, GetCurrentUserUseCase getCurrentUserUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRegisterRequest request) {
        AuthResponse response = registerUseCase.execute(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request) {
        AuthResponse response = loginUseCase.execute(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserDTO user = getCurrentUserUseCase.execute(userId);
        return ResponseEntity.ok(user);
    }
}

