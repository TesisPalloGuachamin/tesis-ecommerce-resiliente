package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.application.usecase.GetProductsUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.GetProductByIdUseCase;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final GetProductsUseCase getProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;

    public ProductController(GetProductsUseCase getProductsUseCase, GetProductByIdUseCase getProductByIdUseCase) {
        this.getProductsUseCase = getProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = getProductsUseCase.execute();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO product = getProductByIdUseCase.execute(productId);
        return ResponseEntity.ok(product);
    }
}

