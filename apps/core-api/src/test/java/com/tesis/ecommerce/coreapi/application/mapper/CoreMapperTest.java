package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ProductDTO;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.UserDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoreMapperTest {

    private final ProductMapper productMapper = new ProductMapper();
    private final UserMapper userMapper = new UserMapper();
    private final CartMapper cartMapper = new CartMapper(productMapper);
    private final CheckoutMapper checkoutMapper = new CheckoutMapper();

    @Test
    void productMapperMapsBothDirectionsAndNulls() {
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
            .id(productId)
            .sku("SKU-001")
            .name("Teclado mecanico")
            .description("Producto de prueba")
            .price(new BigDecimal("45.90"))
            .stock(12)
            .active(true)
            .build();

        ProductDTO dto = productMapper.toDTO(product);
        Product mappedBack = productMapper.toDomain(dto);

        assertThat(dto.getId()).isEqualTo(productId);
        assertThat(dto.getSku()).isEqualTo("SKU-001");
        assertThat(dto.getPrice()).isEqualByComparingTo("45.90");
        assertThat(mappedBack.getName()).isEqualTo("Teclado mecanico");
        assertThat(mappedBack.getStock()).isEqualTo(12);
        assertThat(productMapper.toDTO(null)).isNull();
        assertThat(productMapper.toDomain(null)).isNull();
    }

    @Test
    void userMapperMapsBothDirectionsAndNulls() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
            .id(userId)
            .email("seller@example.com")
            .name("Seller")
            .enabled(true)
            .build();

        UserDTO dto = userMapper.toDTO(user);
        User mappedBack = userMapper.toDomain(dto);

        assertThat(dto.getId()).isEqualTo(userId);
        assertThat(dto.getEmail()).isEqualTo("seller@example.com");
        assertThat(mappedBack.getName()).isEqualTo("Seller");
        assertThat(mappedBack.getEnabled()).isTrue();
        assertThat(userMapper.toDTO(null)).isNull();
        assertThat(userMapper.toDomain(null)).isNull();
    }

    @Test
    void cartMapperMapsCartItemsTotalsAndNulls() {
        User user = User.builder().id(UUID.randomUUID()).build();
        Product product = Product.builder()
            .id(UUID.randomUUID())
            .sku("SKU-CART")
            .name("Mouse")
            .price(new BigDecimal("20.00"))
            .stock(5)
            .active(true)
            .build();
        Cart cart = Cart.builder().id(UUID.randomUUID()).user(user).build();
        CartItem item = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(3)
            .unitPrice(new BigDecimal("20.00"))
            .build();
        cart.setItems(List.of(item));

        var dto = cartMapper.toDTO(cart);
        var itemDto = cartMapper.toItemDTO(item);

        assertThat(dto.getId()).isEqualTo(cart.getId());
        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getTotal()).isEqualByComparingTo("60.00");
        assertThat(itemDto.getProductId()).isEqualTo(product.getId());
        assertThat(itemDto.getProduct().getName()).isEqualTo("Mouse");
        assertThat(itemDto.getTotal()).isEqualByComparingTo("60.00");
        assertThat(cartMapper.toDTO(null)).isNull();
        assertThat(cartMapper.toItemDTO(null)).isNull();
    }

    @Test
    void checkoutMapperMapsStatusAndNull() {
        UUID requestId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        CheckoutRequest request = CheckoutRequest.builder()
            .requestId(requestId)
            .user(user)
            .totalAmount(new BigDecimal("99.90"))
            .status(CheckoutRequest.CheckoutStatus.COMPLETED)
            .build();

        var dto = checkoutMapper.toDTO(request);

        assertThat(dto.getRequestId()).isEqualTo(requestId);
        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("99.90");
        assertThat(dto.getStatus()).isEqualTo("COMPLETED");
        assertThat(checkoutMapper.toDTO(null)).isNull();
    }
}
