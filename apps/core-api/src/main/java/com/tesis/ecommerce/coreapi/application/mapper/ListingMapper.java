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
            .sellerId(listing.getSeller().getId())
            .title(listing.getTitle())
            .description(listing.getDescription())
            .price(listing.getPrice())
            .quantity(listing.getQuantity())
            .status(listing.getStatus().name())
            .createdAt(listing.getCreatedAt().toString())
            .updatedAt(listing.getUpdatedAt().toString())
            .build();
    }
}
