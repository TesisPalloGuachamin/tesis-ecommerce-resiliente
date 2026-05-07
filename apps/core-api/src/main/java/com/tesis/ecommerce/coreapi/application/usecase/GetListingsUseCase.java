package com.tesis.ecommerce.coreapi.application.usecase;

import com.tesis.ecommerce.coreapi.application.mapper.ListingMapper;
import com.tesis.ecommerce.coreapi.domain.port.out.ListingRepository;
import com.tesis.ecommerce.coreapi.infrastructure.adapter.in.web.dto.ListingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetListingsUseCase {

    private final ListingRepository listingRepository;
    private final ListingMapper listingMapper;

    public GetListingsUseCase(ListingRepository listingRepository, ListingMapper listingMapper) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
    }

    public List<ListingDTO> execute() {
        return listingRepository.findAllActive().stream()
            .map(listingMapper::toDTO)
            .collect(Collectors.toList());
    }
}
