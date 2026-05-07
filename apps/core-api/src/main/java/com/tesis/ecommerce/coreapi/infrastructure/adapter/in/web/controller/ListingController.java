package com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.controller;

import com.tesis.ecommerce.coreapi.application.usecase.CreateListingUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.GetListingByIdUseCase;
import com.tesis.ecommerce.coreapi.application.usecase.GetListingsUseCase;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.CreateListingRequest;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
public class ListingController {

    private final CreateListingUseCase createListingUseCase;
    private final GetListingsUseCase getListingsUseCase;
    private final GetListingByIdUseCase getListingByIdUseCase;

    public ListingController(CreateListingUseCase createListingUseCase, GetListingsUseCase getListingsUseCase,
                             GetListingByIdUseCase getListingByIdUseCase) {
        this.createListingUseCase = createListingUseCase;
        this.getListingsUseCase = getListingsUseCase;
        this.getListingByIdUseCase = getListingByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<ListingDTO> createListing(Authentication authentication,
                                                    @Valid @RequestBody CreateListingRequest request) {
        UUID sellerId = (UUID) authentication.getPrincipal();
        ListingDTO listing = createListingUseCase.execute(
            sellerId,
            request.getTitle(),
            request.getDescription(),
            request.getPrice(),
            request.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(listing);
    }

    @GetMapping
    public ResponseEntity<List<ListingDTO>> getListings() {
        return ResponseEntity.ok(getListingsUseCase.execute());
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable UUID listingId) {
        return ResponseEntity.ok(getListingByIdUseCase.execute(listingId));
    }
}
