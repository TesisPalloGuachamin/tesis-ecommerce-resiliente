package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.port.out.ProductRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ProductDTO;
import com.tesis.ecommerce.coreapi.application.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductByIdUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductDTO execute(UUID productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return productMapper.toDTO(product);
    }
}

