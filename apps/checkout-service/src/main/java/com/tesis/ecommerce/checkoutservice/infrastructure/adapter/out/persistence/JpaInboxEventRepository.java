package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.checkoutservice.domain.model.InboxEvent;
import com.tesis.ecommerce.checkoutservice.domain.port.out.InboxEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInboxEventRepository extends JpaRepository<InboxEvent, UUID>, InboxEventRepository {

    @Override
    Optional<InboxEvent> findByEventId(UUID eventId);

}

