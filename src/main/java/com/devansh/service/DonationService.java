package com.devansh.service;

import com.devansh.model.User;
import com.devansh.request.DonationCreationRequest;
import com.devansh.response.DonationDto;
import com.devansh.response.PageableDonationDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface DonationService {

    DonationDto createDonation(@Valid DonationCreationRequest request, User user);
    DonationDto getDonationById(Integer donationId);
    List<DonationDto> getDonationsByUserId(Integer userId);
    void deleteDonationById(Integer donationId);
    DonationDto updateDonationById(Integer donationId, @Valid DonationCreationRequest request, User user);
    PageableDonationDto getFilteredDonations(String category, Integer minQuantity, LocalDateTime beforeExpiry, Pageable pageable);

}
