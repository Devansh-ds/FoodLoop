package com.devansh.response;

import com.devansh.model.DonationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DonationDto(
        String title,
        Integer quantity,
        String locationText,
        LocalDateTime expiresAt,
        String imageUrl,
        LocalDateTime createdAt,
        String description,
        Integer categoryId,
        Integer userId,
        Integer id,
        DonationStatus status
) {
}
