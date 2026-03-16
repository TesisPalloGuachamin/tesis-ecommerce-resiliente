package com.tesis.ecommerce.checkoutservice.application.usecase;

import com.tesis.ecommerce.checkoutservice.application.dto.CheckoutRequestedEvent;
import com.tesis.ecommerce.checkoutservice.domain.model.*;
import com.tesis.ecommerce.checkoutservice.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProcessCheckoutUseCase {

    private final OrderRepository orderRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PaymentPort paymentPort;
    private final EventPublisherPort eventPublisherPort;
    private final InboxEventRepository inboxEventRepository;

    public Order execute(CheckoutRequestedEvent event) {
        log.info("Processing checkout for request: {}", event.getEventId());

        // Check idempotency
        var existingCheckout = inboxEventRepository.findByEventId(event.getEventId());
        if (existingCheckout.isPresent()) {
            log.info("Checkout request {} already processed", event.getEventId());
            var existingOrder = orderRepository.findByCheckoutRequestId(event.getEventId());
            if (existingOrder.isPresent()) {
                return existingOrder.get();
            }
            throw new RuntimeException("Inbox event exists but order not found");
        }

        // Record inbox event
        var inboxEvent = InboxEvent.builder()
                .eventId(event.getEventId())
                .eventType("checkout.requested")
                .payload(event.toString())
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();
        inboxEventRepository.save(inboxEvent);

        // Create Order
        var orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var orderToSave = Order.builder()
                .orderNumber(orderNumber)
                .checkoutRequestId(event.getEventId())
                .userId(event.getUserId())
                .status("PENDING")
                .totalAmount(event.getTotalAmount())
                .createdAt(LocalDateTime.now())
                .build();
        var savedOrder = orderRepository.save(orderToSave);

        // Create OrderItems
        var items = event.getItems().stream()
                .map(item -> OrderItem.builder()
                        .order(savedOrder)
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
        savedOrder.setItems(items);
        var order = orderRepository.save(savedOrder);

        // Publish checkout.accepted
        eventPublisherPort.publishCheckoutAccepted(event.getEventId(), order.getId());

        // Create PaymentAttempt
        var paymentAttempt = PaymentAttempt.builder()
                .orderId(order.getId())
                .amount(event.getTotalAmount())
                .status("PENDING")
                .attemptNumber(1)
                .createdAt(LocalDateTime.now())
                .build();
        paymentAttempt = paymentAttemptRepository.save(paymentAttempt);

        // Execute Payment
        var paymentResult = paymentPort.executePayment(order.getId(), event.getTotalAmount());
        paymentAttempt.setStatus(paymentResult.success ? "SUCCESS" : "FAILED");
        paymentAttempt.setErrorMessage(paymentResult.errorMessage);
        paymentAttempt.setUpdatedAt(LocalDateTime.now());
        paymentAttemptRepository.save(paymentAttempt);

        // Publish payment.processed
        eventPublisherPort.publishPaymentProcessed(order.getId(), paymentResult.transactionId, paymentResult.success);

        // Update order status and publish result
        if (paymentResult.success) {
            order.setStatus("COMPLETED");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            eventPublisherPort.publishCheckoutCompleted(event.getEventId(), order.getId());
            log.info("Checkout completed successfully for order: {}", order.getOrderNumber());
        } else {
            order.setStatus("FAILED");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            eventPublisherPort.publishCheckoutFailed(event.getEventId(), paymentResult.errorMessage);
            log.error("Checkout failed for order: {}, reason: {}", order.getOrderNumber(), paymentResult.errorMessage);
        }

        // Mark inbox event as processed
        inboxEvent.setProcessed(true);
        inboxEvent.setProcessedAt(LocalDateTime.now());
        inboxEventRepository.save(inboxEvent);

        return order;
    }

}



