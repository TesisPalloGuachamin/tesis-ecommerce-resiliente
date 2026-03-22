package com.tesis.ecommerce.checkoutservice.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.checkoutservice.domain.model.PaymentAttempt;
import com.tesis.ecommerce.checkoutservice.domain.port.out.PaymentAttemptRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JpaPaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID>, PaymentAttemptRepository {

}

