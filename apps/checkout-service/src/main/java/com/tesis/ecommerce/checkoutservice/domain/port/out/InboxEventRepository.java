package com.tesis.ecommerce.checkoutservice.domain.port.out;

import com.tesis.ecommerce.checkoutservice.domain.model.InboxEvent;
import java.util.Optional;
import java.util.UUID;

public interface InboxEventRepository {

    InboxEvent save(InboxEvent event);

    Optional<InboxEvent> findByEventId(UUID eventId);

}

