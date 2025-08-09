package com.devansh.service.Impl;

import com.devansh.exception.DonationException;
import com.devansh.mapper.DonationMapper;
import com.devansh.model.Category;
import com.devansh.model.Donation;
import com.devansh.model.DonationStatus;
import com.devansh.model.User;
import com.devansh.repo.DonationRepository;
import com.devansh.request.DonationCreationRequest;
import com.devansh.response.DonationDto;
import com.devansh.response.PageableDonationDto;
import com.devansh.service.CategoryService;
import com.devansh.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final CategoryService categoryService;

    @Override
    public DonationDto createDonation(@Valid DonationCreationRequest request, User user) {
        Category category = categoryService.getCategoryById(request.categoryId());

        Donation donation = Donation.builder()
                .createdAt(LocalDateTime.now())
                .description(request.description())
                .donor(user)
                .title(request.title())
                .expiresAt(request.expiresAt())
                .locationText(request.locationText())
                .imageUrl(request.imageUrl())
                .status(DonationStatus.AVAILABLE)
                .category(category)
                .quantity(request.quantity())
                .build();

        Donation savedDonation = donationRepository.save(donation);
        return donationMapper.toDonationDto(savedDonation);
    }

    @Override
    public DonationDto getDonationById(Integer donationId) {
        Donation savedDonation = findDonationUnsafeById(donationId);
        return donationMapper.toDonationDto(savedDonation);
    }

    @Override
    public List<DonationDto> getDonationsByUserId(Integer userId) {
        List<Donation> allDonations = donationRepository.findByDonorId(userId);
        return allDonations.stream()
                .map(donationMapper::toDonationDto)
                .toList();
    }

    @Override
    public void deleteDonationById(Integer donationId) {
        donationRepository
                .findById(donationId)
                .ifPresent(donation -> donationRepository.deleteById(donationId));
    }

    @Override
    public DonationDto updateDonationById(Integer donationId, DonationCreationRequest request, User user) {
        Donation oldDonation = findDonationUnsafeById(donationId);

        // update only if that donation belongs to that user.
        if (!Objects.equals(oldDonation.getDonor().getId(), user.getId())) {
            throw new DonationException("Donation with id: " + donationId + ", does not belong to user with id: " + user.getId());
        }

        if (request.description() != null) {
            oldDonation.setDescription(request.description());
        }
        if (request.imageUrl() != null) {
            oldDonation.setImageUrl(request.imageUrl());
        }
        if (request.title() != null) {
            oldDonation.setTitle(request.title());
        }
        if (request.expiresAt() != null) {
            oldDonation.setExpiresAt(request.expiresAt());
        }
        if (request.locationText() != null) {
            oldDonation.setLocationText(request.locationText());
        }
        if (request.quantity() != null) {
            oldDonation.setQuantity(request.quantity());
        }
        if (request.categoryId() != null) {
            Category category = categoryService.getCategoryById(request.categoryId());
            oldDonation.setCategory(category);
        }
        Donation updatedDonation = donationRepository.save(oldDonation);
        return donationMapper.toDonationDto(updatedDonation);
    }

    @Override
    public PageableDonationDto getFilteredDonations(String category, Integer minQuantity, LocalDateTime beforeExpiry, Pageable pageable) {
        Page<Donation> filteredDonations = donationRepository.findAvailableDonations(category, minQuantity, beforeExpiry, pageable);

        List<DonationDto> donationDtos = filteredDonations.stream()
                .map(donationMapper::toDonationDto)
                .toList();

        return PageableDonationDto.builder()
                .donationDtos(donationDtos)
                .currentPage(filteredDonations.getNumber())
                .totalPages(filteredDonations.getTotalPages())
                .totalItemsOnPage(filteredDonations.getTotalElements())
                .build();
    }

    private Donation findDonationUnsafeById(Integer donationId) {
        return donationRepository
                .findById(donationId)
                .orElseThrow(() -> new DonationException("Donation not found with id: " + donationId));
    }
}





















