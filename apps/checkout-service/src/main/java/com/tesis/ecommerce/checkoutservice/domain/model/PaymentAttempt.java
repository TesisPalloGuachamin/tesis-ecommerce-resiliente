package com.tesis.ecommerce.checkoutservice.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAttempt {

    @Id
    @Column(columnDefinition = "UUID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "order_id", nullable = false, columnDefinition = "UUID")
    private UUID orderId;

    @JdbcType(NumericJdbcType.class)
    @Column(name = "amount", nullable = false, columnDefinition = "numeric(19,2)")
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "error_message")
    private String errorMessage;

}

