package com.tesis.ecommerce.checkoutservice.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.NumericJdbcType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @Column(columnDefinition = "UUID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false, columnDefinition = "UUID")
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @JdbcType(NumericJdbcType.class)
    @Column(name = "unit_price", nullable = false, columnDefinition = "numeric(19,2)")
    private BigDecimal unitPrice;

    @JdbcType(NumericJdbcType.class)
    @Column(name = "total_price", nullable = false, columnDefinition = "numeric(19,2)")
    private BigDecimal totalPrice;

}

