package com.tesis.ecommerce.coreapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class AbstractIntegrationTest {
}

