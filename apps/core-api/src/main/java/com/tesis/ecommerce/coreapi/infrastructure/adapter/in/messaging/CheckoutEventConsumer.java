package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.messaging;

import com.tesis.ecommerce.coreapi.domain.model.CheckoutRequest;
import com.tesis.ecommerce.coreapi.domain.port.out.CheckoutRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.messaging.dto.CheckoutCompletedEvent;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.messaging.dto.CheckoutFailedEvent;
import com.tesis.ecommerce.coreapi.infrastructure.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class CheckoutEventConsumer {

    private final CheckoutRepository checkoutRepository;

    public CheckoutEventConsumer(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    @RabbitListener(queues = RabbitMqConfig.CHECKOUT_COMPLETED_QUEUE)
    @Transactional
    public void handleCheckoutCompleted(CheckoutCompletedEvent event) {
        try {
            log.info("Received checkout.completed event: {}", event.getEventId());
            
            CheckoutRequest request = checkoutRepository.findByRequestId(event.getCheckoutRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Checkout request not found: " + event.getCheckoutRequestId()));

            request.setStatus(CheckoutRequest.CheckoutStatus.COMPLETED);
            checkoutRepository.save(request);
            
            log.info("Checkout completed for request: {}", event.getCheckoutRequestId());
        } catch (Exception e) {
            log.error("Failed to process checkout.completed event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to process checkout.completed event", e);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.CHECKOUT_FAILED_QUEUE)
    @Transactional
    public void handleCheckoutFailed(CheckoutFailedEvent event) {
        try {
            log.info("Received checkout.failed event: {}", event.getEventId());
            
            CheckoutRequest request = checkoutRepository.findByRequestId(event.getCheckoutRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Checkout request not found: " + event.getCheckoutRequestId()));

            request.setStatus(CheckoutRequest.CheckoutStatus.FAILED);
            checkoutRepository.save(request);
            
            log.info("Checkout failed for request: {}, reason: {}", event.getCheckoutRequestId(), event.getReason());
        } catch (Exception e) {
            log.error("Failed to process checkout.failed event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to process checkout.failed event", e);
        }
    }
}

