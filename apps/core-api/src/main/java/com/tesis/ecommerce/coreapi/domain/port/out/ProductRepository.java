package com.tesis.ecommerce.coreapi.domain.port.out;

import com.tesis.ecommerce.coreapi.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAllActive();
    Product save(Product product);
}

