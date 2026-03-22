package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    List<Product> findByActiveTrue();
}

