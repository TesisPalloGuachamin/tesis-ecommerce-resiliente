package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListingMapperTest {

    private final ListingMapper mapper = new ListingMapper();

    @Test
    void toDTOMapsListingAndAllowsNullUpdatedAt() {
        UUID listingId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 14, 10, 30);

        Listing listing = Listing.builder()
            .id(listingId)
            .seller(User.builder().id(sellerId).build())
            .title("Producto de prueba")
            .description("Descripcion de prueba")
            .price(new BigDecimal("64.25"))
            .quantity(2)
            .status(Listing.ListingStatus.ACTIVE)
            .createdAt(createdAt)
            .updatedAt(null)
            .build();

        ListingDTO dto = mapper.toDTO(listing);

        assertThat(dto.getId()).isEqualTo(listingId);
        assertThat(dto.getSellerId()).isEqualTo(sellerId);
        assertThat(dto.getTitle()).isEqualTo("Producto de prueba");
        assertThat(dto.getDescription()).isEqualTo("Descripcion de prueba");
        assertThat(dto.getPrice()).isEqualByComparingTo("64.25");
        assertThat(dto.getQuantity()).isEqualTo(2);
        assertThat(dto.getStatus()).isEqualTo("ACTIVE");
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt.toString());
        assertThat(dto.getUpdatedAt()).isNull();
    }
}
