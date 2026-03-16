package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.domain.model.Product;
import com.tesis.ecommerce.coreapi.domain.port.out.ProductRepository;
import com.tesis.ecommerce.coreapi.application.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private ProductMapper productMapper;
    private GetProductsUseCase getProductsUseCase;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
        getProductsUseCase = new GetProductsUseCase(productRepository, productMapper);
    }

    @Test
    void testGetAllProducts() {
        Product product1 = Product.builder()
            .id(UUID.randomUUID())
            .sku("PROD001")
            .name("Laptop")
            .price(new BigDecimal("1299.99"))
            .stock(10)
            .active(true)
            .build();

        Product product2 = Product.builder()
            .id(UUID.randomUUID())
            .sku("PROD002")
            .name("Mouse")
            .price(new BigDecimal("29.99"))
            .stock(50)
            .active(true)
            .build();

        when(productRepository.findAllActive()).thenReturn(Arrays.asList(product1, product2));

        var products = getProductsUseCase.execute();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }
}

