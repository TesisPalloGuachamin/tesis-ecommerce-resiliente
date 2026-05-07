package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.ListingMapper;
import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetListingsUseCaseTest {

    @Mock
    private ListingRepository listingRepository;

    private GetListingsUseCase getListingsUseCase;

    @BeforeEach
    void setUp() {
        getListingsUseCase = new GetListingsUseCase(listingRepository, new ListingMapper());
    }

    @Test
    void testListActiveListings() {
        User seller = User.builder()
            .id(UUID.randomUUID())
            .email("seller@example.com")
            .name("Seller")
            .enabled(true)
            .build();
        Listing listing = Listing.builder()
            .seller(seller)
            .title("Camara")
            .description("Camara usada en buen estado")
            .price(new BigDecimal("80.00"))
            .quantity(1)
            .status(Listing.ListingStatus.ACTIVE)
            .build();

        when(listingRepository.findAllActive()).thenReturn(List.of(listing));

        var listings = getListingsUseCase.execute();

        assertEquals(1, listings.size());
        assertEquals("Camara", listings.get(0).getTitle());
        assertEquals(seller.getId(), listings.get(0).getSellerId());
        assertEquals("ACTIVE", listings.get(0).getStatus());
    }
}
