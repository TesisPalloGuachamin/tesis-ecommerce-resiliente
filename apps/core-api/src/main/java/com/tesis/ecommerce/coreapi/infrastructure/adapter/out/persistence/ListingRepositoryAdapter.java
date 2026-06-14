package com.tesis.ecommerce.coreapi.infrastructure.adapter.out.persistence;

import com.tesis.ecommerce.coreapi.domain.model.Listing;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ListingRepositoryAdapter implements ListingRepository {

    private final ListingJpaRepository jpaRepository;

    public ListingRepositoryAdapter(ListingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Listing save(Listing listing) {
        LocalDateTime now = LocalDateTime.now();
        if (listing.getCreatedAt() == null) {
            listing.setCreatedAt(now);
        }
        listing.setUpdatedAt(now);
        return jpaRepository.save(listing);
    }

    @Override
    public List<Listing> findAllActive() {
        return jpaRepository.findByStatusOrderByCreatedAtDesc(Listing.ListingStatus.ACTIVE);
    }

    @Override
    public Optional<Listing> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}
