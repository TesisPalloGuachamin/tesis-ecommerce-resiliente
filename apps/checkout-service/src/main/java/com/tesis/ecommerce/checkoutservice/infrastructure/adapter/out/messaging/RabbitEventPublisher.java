package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.messaging;

import com.tesis.ecommerce.checkoutservice.application.dto.*;
import com.tesis.ecommerce.checkoutservice.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishCheckoutAccepted(UUID checkoutRequestId, UUID orderId) {
        var event = CheckoutAcceptedEvent.builder()
                .eventId(UUID.randomUUID())
                .checkoutRequestId(checkoutRequestId)
                .orderId(orderId)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend("ecommerce.checkout.exchange", "checkout.accepted", event);
        log.info("Published checkout.accepted event for checkout request: {}", checkoutRequestId);
    }

    @Override
    public void publishPaymentProcessed(UUID orderId, String transactionId, boolean success) {
        var event = PaymentProcessedEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(orderId)
                .transactionId(transactionId)
                .success(success)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend("ecommerce.checkout.exchange", "payment.processed", event);
        log.info("Published payment.processed event for order: {}, success: {}", orderId, success);
    }

    @Override
    public void publishCheckoutCompleted(UUID checkoutRequestId, UUID orderId) {
        var event = CheckoutCompletedEvent.builder()
                .eventId(UUID.randomUUID())
                .checkoutRequestId(checkoutRequestId)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend("ecommerce.checkout.exchange", "checkout.completed", event);
        log.info("Published checkout.completed event for checkout request: {}", checkoutRequestId);
    }

    @Override
    public void publishCheckoutFailed(UUID checkoutRequestId, String reason) {
        var event = CheckoutFailedEvent.builder()
                .eventId(UUID.randomUUID())
                .checkoutRequestId(checkoutRequestId)
                .reason(reason)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend("ecommerce.checkout.exchange", "checkout.failed", event);
        log.info("Published checkout.failed event for checkout request: {}, reason: {}", checkoutRequestId, reason);
    }

}

