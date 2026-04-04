package com.tesis.ecommerce.coreapi.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesis.ecommerce.coreapi.domain.model.OutboxEvent;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPollingScheduler {

    private final OutboxEventJpaRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollAndPublishUnpublishedEvents() {
        try {
            List<OutboxEvent> unpublished = outboxEventRepository.findByPublishedFalse();
            
            if (unpublished.isEmpty()) {
                return;
            }
            
            log.debug("Found {} unpublished events in outbox, attempting to republish", unpublished.size());
            
            for (OutboxEvent event : unpublished) {
                try {
                    Object payloadObject = objectMapper.readValue(event.getPayload(), Object.class);
                    rabbitTemplate.convertAndSend(
                        "ecommerce.checkout.exchange",
                        event.getEventType(),
                        payloadObject
                    );

                    event.setPublished(true);
                    outboxEventRepository.save(event);

                    log.info("Successfully republished event {} with type {}", event.getId(), event.getEventType());
                } catch (Exception e) {
                    log.warn("Failed to republish event {}, will retry in next cycle: {}", event.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in outbox polling scheduler", e);
        }
    }
}

