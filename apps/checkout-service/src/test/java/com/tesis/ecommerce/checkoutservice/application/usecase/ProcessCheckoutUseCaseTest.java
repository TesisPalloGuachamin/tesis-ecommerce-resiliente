package com.tesis.ecommerce.checkoutservice.application.usecase;

import com.tesis.ecommerce.checkoutservice.application.dto.CheckoutRequestedEvent;
import com.tesis.ecommerce.checkoutservice.domain.model.InboxEvent;
import com.tesis.ecommerce.checkoutservice.domain.model.Order;
import com.tesis.ecommerce.checkoutservice.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
                .productId(1L)
                .productName("Test Product")
                .quantity(2)
                .unitPrice(50.0)
                .build();

        testEvent = CheckoutRequestedEvent.builder()
                .eventId("event-123")
                .userId(1L)
                .totalAmount(100.0)
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
                .id(1L)
                .orderNumber("ORD-12345678")
                .checkoutRequestId(testEvent.getEventId())
                .userId(testEvent.getUserId())
                .status("PENDING")
                .totalAmount(testEvent.getTotalAmount())
                .build();

        when(orderRepository.save(any())).thenReturn(mockOrder);

        var paymentResult = new PaymentPort.PaymentResult("txn-123", true, null);
        when(paymentPort.executePayment(anyLong(), anyDouble()))
                .thenReturn(paymentResult);

        // Act
        var result = processCheckoutUseCase.execute(testEvent);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-12345678", result.getOrderNumber());
        verify(inboxEventRepository, times(2)).save(any(InboxEvent.class));
        verify(paymentPort, times(1)).executePayment(anyLong(), anyDouble());
        verify(eventPublisherPort, times(1)).publishCheckoutAccepted(anyString(), anyLong());
        verify(eventPublisherPort, times(1)).publishPaymentProcessed(anyLong(), anyString(), anyBoolean());
        verify(eventPublisherPort, times(1)).publishCheckoutCompleted(anyString(), anyLong());
    }

    @Test
    void testProcessCheckoutIdempotency() {
        // Arrange
        var existingInboxEvent = InboxEvent.builder()
                .id(1L)
                .eventId(testEvent.getEventId())
                .eventType("checkout.requested")
                .processed(true)
                .build();

        when(inboxEventRepository.findByEventId(testEvent.getEventId()))
                .thenReturn(Optional.of(existingInboxEvent));

        var existingOrder = Order.builder()
                .id(1L)
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
        verify(paymentPort, never()).executePayment(anyLong(), anyDouble());
    }

}

