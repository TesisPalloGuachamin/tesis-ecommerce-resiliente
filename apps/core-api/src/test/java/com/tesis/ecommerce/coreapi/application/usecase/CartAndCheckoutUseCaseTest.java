package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.CartMapper;
import com.tesis.ecommerce.coreapi.application.mapper.CheckoutMapper;
import com.tesis.ecommerce.coreapi.application.mapper.ProductMapper;
import com.tesis.ecommerce.coreapi.domain.model.Cart;
import com.tesis.ecommerce.coreapi.domain.model.CartItem;
import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.CartItemRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.CartRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.EventPublisher;
import com.tesis.ecommerce.coreapi.domain.port.out.ProductRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartAndCheckoutUseCaseTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private EventPublisher eventPublisher;

    private CartMapper cartMapper;
    private CheckoutMapper checkoutMapper;
    private User user;
    private Product product;
    private Cart cart;
    private CartItem item;

    @BeforeEach
    void setUp() {
        cartMapper = new CartMapper(new ProductMapper());
        checkoutMapper = new CheckoutMapper();
        user = User.builder().id(UUID.randomUUID()).email("user@example.com").name("User").enabled(true).build();
        product = Product.builder()
            .id(UUID.randomUUID())
            .sku("SKU-USE")
            .name("Producto")
            .price(new BigDecimal("25.00"))
            .stock(5)
            .active(true)
            .build();
        cart = Cart.builder().id(UUID.randomUUID()).user(user).items(new ArrayList<>()).build();
        item = CartItem.builder()
            .id(UUID.randomUUID())
            .cart(cart)
            .product(product)
            .quantity(2)
            .unitPrice(product.getPrice())
            .build();
    }

    @Test
    void addCartItemCreatesCartWhenUserHasNoCart() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var useCase = new AddCartItemUseCase(cartRepository, cartItemRepository, productRepository, userRepository, cartMapper);
        var dto = useCase.execute(user.getId(), product.getId(), 2);

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getTotal()).isEqualByComparingTo("50.00");
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addCartItemRejectsMissingProductAndInsufficientStock() {
        var useCase = new AddCartItemUseCase(cartRepository, cartItemRepository, productRepository, userRepository, cartMapper);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(user.getId(), product.getId(), 1))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Product not found");

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        assertThatThrownBy(() -> useCase.execute(user.getId(), product.getId(), 6))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Not enough stock");
    }

    @Test
    void getCartCreatesEmptyCartWhenItDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var useCase = new GetCartUseCase(cartRepository, userRepository, cartMapper);
        var dto = useCase.execute(user.getId());

        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getItems()).isEmpty();
        assertThat(dto.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void updateCartItemChangesQuantityAndRejectsInvalidCases() {
        cart.getItems().add(item);
        when(cartItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(cart)).thenReturn(cart);

        var useCase = new UpdateCartItemUseCase(cartRepository, cartItemRepository, cartMapper);
        var dto = useCase.execute(user.getId(), item.getId(), 3);

        assertThat(dto.getItems()).singleElement().satisfies(updated -> {
            assertThat(updated.getQuantity()).isEqualTo(3);
            assertThat(updated.getTotal()).isEqualByComparingTo("75.00");
        });

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), item.getId(), 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unauthorized");
        assertThatThrownBy(() -> useCase.execute(user.getId(), item.getId(), 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Quantity must be greater than 0");
    }

    @Test
    void deleteCartItemRemovesItemAndRejectsUnauthorizedUser() {
        cart.getItems().add(item);
        when(cartItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartRepository.save(cart)).thenReturn(cart);

        var useCase = new DeleteCartItemUseCase(cartRepository, cartItemRepository, cartMapper);
        var dto = useCase.execute(user.getId(), item.getId());

        assertThat(dto.getItems()).isEmpty();
        assertThat(dto.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(cartItemRepository).delete(item);

        cart.getItems().add(item);
        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), item.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unauthorized");
    }

    @Test
    void checkoutPersistsPendingRequestAndPublishesEvent() {
        cart.getItems().add(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(checkoutRepository.save(any(CheckoutRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var useCase = new CheckoutUseCase(cartRepository, checkoutRepository, userRepository, eventPublisher, checkoutMapper);
        var dto = useCase.execute(user.getId(), cart.getId());

        assertThat(dto.getRequestId()).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("50.00");
        assertThat(dto.getStatus()).isEqualTo("PENDING");

        ArgumentCaptor<UUID> requestIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(eventPublisher).publishCheckoutRequested(
            requestIdCaptor.capture(),
            eq(user.getId()),
            eq(new BigDecimal("50.00")),
            eq(cart.getItems())
        );
        assertThat(requestIdCaptor.getValue()).isEqualTo(dto.getRequestId());
    }

    @Test
    void checkoutRejectsMissingCartAndEmptyCart() {
        var useCase = new CheckoutUseCase(cartRepository, checkoutRepository, userRepository, eventPublisher, checkoutMapper);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(user.getId(), cart.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cart not found");

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        assertThatThrownBy(() -> useCase.execute(user.getId(), cart.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cart is empty");
        verify(eventPublisher, never()).publishCheckoutRequested(any(), any(), any(), any());
    }

    @Test
    void getCheckoutRequestReturnsDtoAndFailsWhenMissing() {
        UUID requestId = UUID.randomUUID();
        CheckoutRequest request = CheckoutRequest.builder()
            .requestId(requestId)
            .user(user)
            .cart(cart)
            .totalAmount(new BigDecimal("50.00"))
            .status(CheckoutRequest.CheckoutStatus.COMPLETED)
            .build();
        when(checkoutRepository.findByRequestId(requestId)).thenReturn(Optional.of(request));

        var useCase = new GetCheckoutRequestUseCase(checkoutRepository, checkoutMapper);
        var dto = useCase.execute(requestId);

        assertThat(dto.getStatus()).isEqualTo("COMPLETED");
        assertThat(dto.getTotalAmount()).isEqualByComparingTo("50.00");

        UUID missingId = UUID.randomUUID();
        when(checkoutRepository.findByRequestId(missingId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(missingId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Checkout request not found");
    }
}
