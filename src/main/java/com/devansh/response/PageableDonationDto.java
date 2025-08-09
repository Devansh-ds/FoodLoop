package com.devansh.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PageableDonationDto(
        List<DonationDto> donationDtos,
        Integer currentPage,
        Integer totalPages,
        Long totalItemsOnPage
) {
}
