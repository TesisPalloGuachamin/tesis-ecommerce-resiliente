package com.tesis.ecommerce.coreapi.application.mapper;

import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import org.springframework.stereotype.Component;

@Component
public class ListingMapper {

    public ListingDTO toDTO(Listing listing) {
        if (listing == null) {
            return null;
        }
        return ListingDTO.builder()
            .id(listing.getId())
            .sellerId(listing.getSeller() != null ? listing.getSeller().getId() : null)
            .title(listing.getTitle())
            .description(listing.getDescription())
            .price(listing.getPrice())
            .quantity(listing.getQuantity())
            .status(listing.getStatus() != null ? listing.getStatus().name() : null)
            .createdAt(listing.getCreatedAt() != null ? listing.getCreatedAt().toString() : null)
            .updatedAt(listing.getUpdatedAt() != null ? listing.getUpdatedAt().toString() : null)
            .build();
    }
}
