package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.ListingMapper;
import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.model.User;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import com.tesis.ecommerce.coreapi.domain.port.out.UserRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class CreateListingUseCase {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingMapper listingMapper;

    public CreateListingUseCase(ListingRepository listingRepository, UserRepository userRepository,
                                ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.listingMapper = listingMapper;
    }

    public ListingDTO execute(UUID sellerId, String title, String description, BigDecimal price, Integer quantity) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Listing listing = Listing.builder()
            .seller(seller)
            .title(title.trim())
            .description(description == null ? null : description.trim())
            .price(price)
            .quantity(quantity)
            .status(Listing.ListingStatus.ACTIVE)
            .build();

        return listingMapper.toDTO(listingRepository.save(listing));
    }
}
