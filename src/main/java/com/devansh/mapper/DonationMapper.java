package com.devansh.mapper;

import com.devansh.model.Donation;
import com.devansh.response.DonationDto;
import org.springframework.stereotype.Service;

@Service
public class DonationMapper {

    public DonationDto toDonationDto(Donation donation) {

        return DonationDto.builder()
                .title(donation.getTitle())
                .description(donation.getDescription())
                .status(donation.getStatus())
                .quantity(donation.getQuantity())
                .locationText(donation.getLocationText())
                .expiresAt(donation.getExpiresAt())
                .imageUrl(donation.getImageUrl())
                .createdAt(donation.getCreatedAt())
                .id(donation.getId())
                .userId(donation.getDonor().getId())
                .categoryId(donation.getCategory().getId())
                .build();
    }
}
