package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.ListingMapper;
import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateListingUseCaseTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserRepository userRepository;

    private CreateListingUseCase createListingUseCase;

    @BeforeEach
    void setUp() {
        createListingUseCase = new CreateListingUseCase(listingRepository, userRepository, new ListingMapper());
    }

    @Test
    void testCreateListingAssociatesAuthenticatedSeller() {
        UUID sellerId = UUID.randomUUID();
        User seller = User.builder()
            .id(sellerId)
            .email("seller@example.com")
            .name("Seller")
            .enabled(true)
            .build();

        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var listing = createListingUseCase.execute(
            sellerId,
            "  Bicicleta urbana  ",
            "  Publicacion demo  ",
            new BigDecimal("125.50"),
            1
        );

        assertNotNull(listing.getId());
        assertEquals(sellerId, listing.getSellerId());
        assertEquals("Bicicleta urbana", listing.getTitle());
        assertEquals("Publicacion demo", listing.getDescription());
        assertEquals(new BigDecimal("125.50"), listing.getPrice());
        assertEquals(1, listing.getQuantity());
        assertEquals("ACTIVE", listing.getStatus());
    }

    @Test
    void testCreateListingFailsWhenSellerDoesNotExist() {
        UUID sellerId = UUID.randomUUID();
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
            createListingUseCase.execute(sellerId, "Producto", null, new BigDecimal("10.00"), 1)
        );
    }
}
