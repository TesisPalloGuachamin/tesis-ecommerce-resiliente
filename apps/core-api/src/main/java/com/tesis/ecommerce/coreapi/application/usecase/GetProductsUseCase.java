package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.port.out.ProductRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ProductDTO;
import com.tesis.ecommerce.coreapi.application.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetProductsUseCase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductsUseCase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductDTO> execute() {
        return productRepository.findAllActive().stream()
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }
}

