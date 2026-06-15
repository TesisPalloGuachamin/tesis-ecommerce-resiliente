package com.tesis.ecommerce.checkoutservice.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CheckoutDomainModelTest {

    @Test
    void checkoutRequestStoresTraceableState() {
        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CheckoutRequest request = CheckoutRequest.builder()
            .requestId(requestId)
            .userId(userId)
            .status("PENDING")
            .totalAmount(new BigDecimal("120.00"))
            .createdAt(now)
            .updatedAt(now)
            .build();

        assertThat(request.getId()).isNotNull();
        assertThat(request.getRequestId()).isEqualTo(requestId);
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getStatus()).isEqualTo("PENDING");
        assertThat(request.getTotalAmount()).isEqualByComparingTo("120.00");
        assertThat(request.getCreatedAt()).isEqualTo(now);
        assertThat(request.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void orderAndItemsRepresentCompletedCheckout() {
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Order order = Order.builder()
            .id(orderId)
            .orderNumber("ORD-TEST")
            .checkoutRequestId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .status("COMPLETED")
            .totalAmount(new BigDecimal("40.00"))
            .createdAt(LocalDateTime.now())
            .build();
        OrderItem item = OrderItem.builder()
            .order(order)
            .productId(productId)
            .productName("Libro")
            .quantity(2)
            .unitPrice(new BigDecimal("20.00"))
            .totalPrice(new BigDecimal("40.00"))
            .build();

        order.getItems().add(item);

        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getOrderNumber()).isEqualTo("ORD-TEST");
        assertThat(order.getStatus()).isEqualTo("COMPLETED");
        assertThat(order.getItems()).containsExactly(item);
        assertThat(item.getOrder()).isSameAs(order);
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getTotalPrice()).isEqualByComparingTo("40.00");
    }

    @Test
    void paymentAttemptAndInboxEventTrackProcessingMetadata() {
        LocalDateTime now = LocalDateTime.now();
        UUID orderId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        PaymentAttempt paymentAttempt = PaymentAttempt.builder()
            .orderId(orderId)
            .amount(new BigDecimal("25.00"))
            .status("FAILED")
            .attemptNumber(1)
            .createdAt(now)
            .updatedAt(now)
            .errorMessage("declined")
            .build();
        InboxEvent inboxEvent = InboxEvent.builder()
            .eventId(eventId)
            .eventType("checkout.requested")
            .payload("{masked}")
            .processed(true)
            .createdAt(now)
            .processedAt(now)
            .build();

        assertThat(paymentAttempt.getId()).isNotNull();
        assertThat(paymentAttempt.getOrderId()).isEqualTo(orderId);
        assertThat(paymentAttempt.getStatus()).isEqualTo("FAILED");
        assertThat(paymentAttempt.getErrorMessage()).isEqualTo("declined");
        assertThat(inboxEvent.getId()).isNotNull();
        assertThat(inboxEvent.getEventId()).isEqualTo(eventId);
        assertThat(inboxEvent.getProcessed()).isTrue();
        assertThat(inboxEvent.getProcessedAt()).isEqualTo(now);
    }
}
