package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesis.ecommerce.coreapi.domain.model.OutboxEvent;
import com.tesis.ecommerce.coreapi.domain.port.out.EventPublisher;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence.OutboxEventJpaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class EventPublisherAdapter implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final OutboxEventJpaRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public EventPublisherAdapter(RabbitTemplate rabbitTemplate,
                                OutboxEventJpaRepository outboxEventRepository,
                                ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void publish(OutboxEvent event) {
        try {
            // Save to outbox first (transactional)
            outboxEventRepository.save(event);
            
            // Then attempt to publish
            try {
                String routingKey = event.getEventType();
                rabbitTemplate.convertAndSend("ecommerce.checkout.exchange", routingKey, event.getPayload());
                event.setPublished(true);
                outboxEventRepository.save(event);
                log.info("Published event: {}", event.getId());
            } catch (Exception e) {
                log.warn("Failed to publish event to RabbitMQ immediately, will retry via polling: {}", event.getId());
                // Polling job will retry this
            }
        } catch (Exception e) {
            log.error("Failed to save event to outbox", e);
            throw new RuntimeException("Failed to process event", e);
        }
    }

    @Override
    @Transactional
    public void publishCheckoutRequested(UUID requestId, UUID userId, Double totalAmount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID());
        payload.put("requestId", requestId);
        payload.put("userId", userId);
        payload.put("totalAmount", totalAmount);
        payload.put("timestamp", System.currentTimeMillis());

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);

            OutboxEvent event = OutboxEvent.builder()
                .eventType("checkout.requested")
                .payload(payloadJson)
                .published(false)
                .build();

            publish(event);
        } catch (Exception e) {
            log.error("Failed to publish checkout.requested event", e);
            throw new RuntimeException("Failed to publish checkout.requested event", e);
        }
    }
}

