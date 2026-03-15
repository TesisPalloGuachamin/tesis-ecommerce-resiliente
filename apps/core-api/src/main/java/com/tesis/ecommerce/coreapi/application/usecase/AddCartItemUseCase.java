package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.CartRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.CartItemRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.ProductRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CartDTO;
import com.tesis.ecommerce.coreapi.application.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AddCartItemUseCase {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public AddCartItemUseCase(CartRepository cartRepository, CartItemRepository cartItemRepository,
                             ProductRepository productRepository, UserRepository userRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    public CartDTO execute(UUID userId, UUID productId, Integer quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }

        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .user(user)
                    .build();
                return cartRepository.save(newCart);
            });

        CartItem cartItem = CartItem.builder()
            .cart(cart)
            .product(product)
            .quantity(quantity)
            .unitPrice(product.getPrice())
            .build();

        cartItemRepository.save(cartItem);
        cart.getItems().add(cartItem);
        cartRepository.save(cart);

        return cartMapper.toDTO(cart);
    }
}

