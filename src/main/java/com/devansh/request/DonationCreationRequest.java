package com.devansh.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record DonationCreationRequest(

        @NotBlank String title,
        @Positive Integer quantity,
        String locationText,
        @Future(message = "Expiry date should be of future") LocalDateTime expiresAt,
        String imageUrl,
        String description,
        Integer categoryId
) {
}
