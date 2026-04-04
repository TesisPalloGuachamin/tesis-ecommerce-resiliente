package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.application.usecase.CheckoutUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.GetCheckoutRequestUseCase;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CheckoutRequestDTO;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CreateCheckoutRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {

    private final CheckoutUseCase checkoutUseCase;
    private final GetCheckoutRequestUseCase getCheckoutRequestUseCase;

    public CheckoutController(CheckoutUseCase checkoutUseCase, GetCheckoutRequestUseCase getCheckoutRequestUseCase) {
        this.checkoutUseCase = checkoutUseCase;
        this.getCheckoutRequestUseCase = getCheckoutRequestUseCase;
    }

    @PostMapping
    public ResponseEntity<CheckoutRequestDTO> checkout(
            Authentication authentication,
            @Valid @RequestBody CreateCheckoutRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        CheckoutRequestDTO response = checkoutUseCase.execute(userId, request.getCartId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<CheckoutRequestDTO> getCheckoutStatus(@PathVariable UUID requestId) {
        CheckoutRequestDTO response = getCheckoutRequestUseCase.execute(requestId);
        return ResponseEntity.ok(response);
    }
}
