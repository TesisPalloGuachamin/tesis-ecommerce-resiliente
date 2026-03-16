package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.in.messaging;

import com.tesis.ecommerce.checkoutservice.application.dto.CheckoutRequestedEvent;
import com.tesis.ecommerce.checkoutservice.application.usecase.ProcessCheckoutUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.tesis.ecommerce.checkoutservice.infrastructure.config.RabbitMqConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutEventConsumer {

    private final ProcessCheckoutUseCase processCheckoutUseCase;

    @RabbitListener(queues = RabbitMqConfig.CHECKOUT_REQUESTED_QUEUE)
    public void handleCheckoutRequested(CheckoutRequestedEvent event) {
        log.info("Received checkout.requested event: {}", event.getEventId());
        try {
            processCheckoutUseCase.execute(event);
            log.info("Checkout processed successfully for event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing checkout event: {}", event.getEventId(), e);
            // Note: In production, implement dead-letter queue or retry logic
            throw e;
        }
    }

}

