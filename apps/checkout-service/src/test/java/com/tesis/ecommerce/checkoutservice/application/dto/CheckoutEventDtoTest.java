package com.tesis.ecommerce.checkoutservice.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CheckoutEventDtoTest {

    @Test
    void checkoutRequestedEventContainsItemsAndTotals() {
        UUID eventId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CheckoutRequestedEvent.CartItemDto item = CheckoutRequestedEvent.CartItemDto.builder()
            .productId(productId)
            .productName("Camara")
            .quantity(2)
            .unitPrice(new BigDecimal("55.00"))
            .build();

        CheckoutRequestedEvent event = CheckoutRequestedEvent.builder()
            .eventId(eventId)
            .requestId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .totalAmount(new BigDecimal("110.00"))
            .items(List.of(item))
            .timestamp(1000L)
            .build();

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getItems()).singleElement().satisfies(mapped -> {
            assertThat(mapped.getProductId()).isEqualTo(productId);
            assertThat(mapped.getProductName()).isEqualTo("Camara");
            assertThat(mapped.getQuantity()).isEqualTo(2);
            assertThat(mapped.getUnitPrice()).isEqualByComparingTo("55.00");
        });
        assertThat(event.getTotalAmount()).isEqualByComparingTo("110.00");
    }

    @Test
    void resultEventsExposeTraceIdentifiers() {
        UUID checkoutRequestId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        CheckoutAcceptedEvent accepted = CheckoutAcceptedEvent.builder()
            .eventId(UUID.randomUUID())
            .checkoutRequestId(checkoutRequestId)
            .orderId(orderId)
            .userId(UUID.randomUUID())
            .totalAmount(45.50)
            .timestamp(10L)
            .build();
        CheckoutCompletedEvent completed = CheckoutCompletedEvent.builder()
            .eventId(UUID.randomUUID())
            .checkoutRequestId(checkoutRequestId)
            .timestamp(20L)
            .build();
        CheckoutFailedEvent failed = CheckoutFailedEvent.builder()
            .eventId(UUID.randomUUID())
            .checkoutRequestId(checkoutRequestId)
            .reason("payment failed")
            .timestamp(30L)
            .build();
        PaymentProcessedEvent payment = PaymentProcessedEvent.builder()
            .eventId(UUID.randomUUID())
            .orderId(orderId)
            .transactionId("stripe_sim_test")
            .success(true)
            .amount(45.50)
            .timestamp(40L)
            .build();

        assertThat(accepted.getCheckoutRequestId()).isEqualTo(checkoutRequestId);
        assertThat(accepted.getOrderId()).isEqualTo(orderId);
        assertThat(completed.getCheckoutRequestId()).isEqualTo(checkoutRequestId);
        assertThat(failed.getReason()).isEqualTo("payment failed");
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getTransactionId()).isEqualTo("stripe_sim_test");
        assertThat(payment.isSuccess()).isTrue();
    }

    @Test
    void noArgsConstructorsSupportFrameworkDeserialization() {
        CheckoutRequestedEvent requested = new CheckoutRequestedEvent();
        requested.setEventId(UUID.randomUUID());
        requested.setTotalAmount(new BigDecimal("10.00"));
        CheckoutRequestedEvent.CartItemDto item = new CheckoutRequestedEvent.CartItemDto();
        item.setProductName("Producto");
        item.setQuantity(1);

        CheckoutFailedEvent failed = new CheckoutFailedEvent();
        failed.setReason("invalid");

        assertThat(requested.getEventId()).isNotNull();
        assertThat(requested.getTotalAmount()).isEqualByComparingTo("10.00");
        assertThat(item.getProductName()).isEqualTo("Producto");
        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(failed.getReason()).isEqualTo("invalid");
    }
}
