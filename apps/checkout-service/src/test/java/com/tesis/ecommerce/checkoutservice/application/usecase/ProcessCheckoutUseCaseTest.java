package com.tesis.ecommerce.checkoutservice.application.usecase;

import com.tesis.ecommerce.checkoutservice.application.dto.CheckoutRequestedEvent;
import com.tesis.ecommerce.checkoutservice.domain.model.InboxEvent;
import com.tesis.ecommerce.checkoutservice.domain.model.Order;
import com.tesis.ecommerce.checkoutservice.domain.model.PaymentAttempt;
import com.tesis.ecommerce.checkoutservice.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessCheckoutUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentAttemptRepository paymentAttemptRepository;

    @Mock
    private PaymentPort paymentPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private InboxEventRepository inboxEventRepository;

    @InjectMocks
    private ProcessCheckoutUseCase processCheckoutUseCase;

    private CheckoutRequestedEvent testEvent;

    @BeforeEach
    void setUp() {
        var item = CheckoutRequestedEvent.CartItemDto.builder()
                .productId(UUID.randomUUID())
                .productName("Test Product")
                .quantity(2)
                .unitPrice(new BigDecimal("50.00"))
                .build();

        testEvent = CheckoutRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .requestId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .totalAmount(new BigDecimal("100.00"))
                .items(List.of(item))
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Test
    void testProcessCheckoutSuccessfully() {
        // Arrange
        when(inboxEventRepository.findByEventId(testEvent.getEventId()))
                .thenReturn(Optional.empty());

        var mockOrder = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-12345678")
                .checkoutRequestId(testEvent.getEventId())
                .userId(testEvent.getUserId())
                .status("PENDING")
                .totalAmount(testEvent.getTotalAmount())
                .build();

        when(orderRepository.save(any())).thenReturn(mockOrder);

        var mockPaymentAttempt = new PaymentAttempt();
        mockPaymentAttempt.setId(UUID.randomUUID());
        mockPaymentAttempt.setOrderId(mockOrder.getId());
        mockPaymentAttempt.setAmount(testEvent.getTotalAmount());
        mockPaymentAttempt.setStatus("PENDING");
        mockPaymentAttempt.setAttemptNumber(1);
        
        when(paymentAttemptRepository.save(any())).thenReturn(mockPaymentAttempt);

        var paymentResult = new PaymentPort.PaymentResult("txn-123", true, null);
        when(paymentPort.executePayment(any(UUID.class), any(BigDecimal.class)))
                .thenReturn(paymentResult);

        // Act
        var result = processCheckoutUseCase.execute(testEvent);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(inboxEventRepository, times(2)).save(any(InboxEvent.class));
        verify(paymentPort, times(1)).executePayment(any(UUID.class), any(BigDecimal.class));
        verify(eventPublisherPort, times(1)).publishCheckoutAccepted(any(UUID.class), any(UUID.class));
        verify(eventPublisherPort, times(1)).publishPaymentProcessed(any(UUID.class), anyString(), anyBoolean());
        verify(eventPublisherPort, times(1)).publishCheckoutCompleted(any(UUID.class), any(UUID.class));
    }

    @Test
    void testProcessCheckoutIdempotency() {
        // Arrange
        var existingInboxEvent = InboxEvent.builder()
                .id(UUID.randomUUID())
                .eventId(testEvent.getEventId())
                .eventType("checkout.requested")
                .processed(true)
                .build();

        when(inboxEventRepository.findByEventId(testEvent.getEventId()))
                .thenReturn(Optional.of(existingInboxEvent));

        var existingOrder = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-12345678")
                .checkoutRequestId(testEvent.getEventId())
                .build();

        when(orderRepository.findByCheckoutRequestId(testEvent.getEventId()))
                .thenReturn(Optional.of(existingOrder));

        // Act
        var result = processCheckoutUseCase.execute(testEvent);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(orderRepository, never()).save(any());
        verify(paymentPort, never()).executePayment(any(UUID.class), any(BigDecimal.class));
    }

}
