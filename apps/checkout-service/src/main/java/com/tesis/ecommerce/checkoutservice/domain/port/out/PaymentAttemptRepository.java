package com.tesis.ecommerce.checkoutservice.domain.port.out;

import com.tesis.ecommerce.checkoutservice.domain.model.PaymentAttempt;
import java.util.UUID;

public interface PaymentAttemptRepository {

    PaymentAttempt save(PaymentAttempt attempt);

}

