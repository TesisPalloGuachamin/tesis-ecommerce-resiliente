package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.domain.port.out.CartRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.CartItemRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CartDTO;
import com.tesis.ecommerce.coreapi.application.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateCartItemUseCase {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    public UpdateCartItemUseCase(CartRepository cartRepository, CartItemRepository cartItemRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    public CartDTO execute(UUID userId, UUID itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        cartRepository.save(cart);

        return cartMapper.toDTO(cart);
    }
}

