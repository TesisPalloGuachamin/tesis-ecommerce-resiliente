package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.CartRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.EventPublisher;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CheckoutRequestDTO;
import com.tesis.ecommerce.coreapi.application.mapper.CheckoutMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CheckoutUseCase {

    private final CartRepository cartRepository;
    private final CheckoutRepository checkoutRepository;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final CheckoutMapper checkoutMapper;

    public CheckoutUseCase(CartRepository cartRepository, CheckoutRepository checkoutRepository,
                          UserRepository userRepository, EventPublisher eventPublisher, CheckoutMapper checkoutMapper) {
        this.cartRepository = cartRepository;
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.checkoutMapper = checkoutMapper;
    }

    public CheckoutRequestDTO execute(UUID userId, UUID cartId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = cart.getTotal();
        UUID requestId = UUID.randomUUID();

        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
            .requestId(requestId)
            .user(user)
            .cart(cart)
            .totalAmount(totalAmount)
            .status(CheckoutRequest.CheckoutStatus.PENDING)
            .build();

        CheckoutRequest savedRequest = checkoutRepository.save(checkoutRequest);
        eventPublisher.publishCheckoutRequested(requestId, userId, totalAmount);

        return checkoutMapper.toDTO(savedRequest);
    }
}

