package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.ListingMapper;
import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetListingByIdUseCase {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;

    public GetListingByIdUseCase(ListingRepository listingRepository, ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
    }

    public ListingDTO execute(UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        return listingMapper.toDTO(listing);
    }
}
