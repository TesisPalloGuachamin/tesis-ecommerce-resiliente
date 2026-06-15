package com.tesis.ecommerce.coreapi.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoreDomainModelTest {

    @Test
    void cartAndCartItemCalculateTotals() {
        Product product = Product.builder()
            .id(UUID.randomUUID())
            .sku("SKU-TOTAL")
            .name("Producto")
            .price(new BigDecimal("15.25"))
            .stock(10)
            .active(true)
            .build();
        Cart cart = Cart.builder()
            .id(UUID.randomUUID())
            .user(User.builder().id(UUID.randomUUID()).build())
            .build();
        CartItem first = CartItem.builder()
            .cart(cart)
            .product(product)
            .quantity(2)
            .unitPrice(new BigDecimal("15.25"))
            .build();
        CartItem second = CartItem.builder()
            .cart(cart)
            .product(product)
            .quantity(1)
            .unitPrice(new BigDecimal("9.50"))
            .build();

        cart.setItems(List.of(first, second));

        assertThat(first.getTotal()).isEqualByComparingTo("30.50");
        assertThat(second.getTotal()).isEqualByComparingTo("9.50");
        assertThat(cart.getTotal()).isEqualByComparingTo("40.00");
    }

    @Test
    void modelDefaultsRepresentInitialDomainState() {
        User user = new User();
        Product product = new Product();
        Listing listing = Listing.builder()
            .seller(User.builder().id(UUID.randomUUID()).build())
            .title("Listing")
            .price(new BigDecimal("12.00"))
            .quantity(1)
            .build();
        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
            .requestId(UUID.randomUUID())
            .user(User.builder().id(UUID.randomUUID()).build())
            .cart(Cart.builder().user(User.builder().id(UUID.randomUUID()).build()).build())
            .totalAmount(new BigDecimal("12.00"))
            .build();
        OutboxEvent outboxEvent = OutboxEvent.builder()
            .eventType("checkout.requested")
            .payload("{}")
            .build();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(listing.getId()).isNotNull();
        assertThat(listing.getStatus()).isEqualTo(Listing.ListingStatus.ACTIVE);
        assertThat(listing.getCreatedAt()).isNotNull();
        assertThat(listing.getUpdatedAt()).isNotNull();
        assertThat(checkoutRequest.getStatus()).isEqualTo(CheckoutRequest.CheckoutStatus.PENDING);
        assertThat(checkoutRequest.getCreatedAt()).isNotNull();
        assertThat(checkoutRequest.getUpdatedAt()).isNotNull();
        assertThat(outboxEvent.getId()).isNotNull();
        assertThat(outboxEvent.getPublished()).isFalse();
        assertThat(outboxEvent.getCreatedAt()).isNotNull();
    }

    @Test
    void settersAllowUpdatingMutableStateForPersistenceAdapters() {
        CheckoutRequest request = new CheckoutRequest();
        UUID id = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        request.setId(id);
        request.setRequestId(requestId);
        request.setStatus(CheckoutRequest.CheckoutStatus.FAILED);
        request.setTotalAmount(new BigDecimal("33.30"));
        request.setCreatedAt(now);
        request.setUpdatedAt(now);

        assertThat(request.getId()).isEqualTo(id);
        assertThat(request.getRequestId()).isEqualTo(requestId);
        assertThat(request.getStatus()).isEqualTo(CheckoutRequest.CheckoutStatus.FAILED);
        assertThat(request.getTotalAmount()).isEqualByComparingTo("33.30");
        assertThat(request.getCreatedAt()).isEqualTo(now);
        assertThat(request.getUpdatedAt()).isEqualTo(now);
    }
}
