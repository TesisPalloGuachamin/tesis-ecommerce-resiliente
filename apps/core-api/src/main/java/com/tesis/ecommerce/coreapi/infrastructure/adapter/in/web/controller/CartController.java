package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.application.usecase.*;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CartDTO;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.AddCartItemRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UpdateCartItemRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final DeleteCartItemUseCase deleteCartItemUseCase;

    public CartController(GetCartUseCase getCartUseCase, AddCartItemUseCase addCartItemUseCase,
                         UpdateCartItemUseCase updateCartItemUseCase, DeleteCartItemUseCase deleteCartItemUseCase) {
        this.getCartUseCase = getCartUseCase;
        this.addCartItemUseCase = addCartItemUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
        this.deleteCartItemUseCase = deleteCartItemUseCase;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        CartDTO cart = getCartUseCase.execute(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItem(Authentication authentication, @RequestBody AddCartItemRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        CartDTO cart = addCartItemUseCase.execute(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateItem(Authentication authentication, 
                                               @PathVariable UUID itemId,
                                               @RequestBody UpdateCartItemRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        CartDTO cart = updateCartItemUseCase.execute(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> deleteItem(Authentication authentication, @PathVariable UUID itemId) {
        UUID userId = (UUID) authentication.getPrincipal();
        CartDTO cart = deleteCartItemUseCase.execute(userId, itemId);
        return ResponseEntity.ok(cart);
    }
}

