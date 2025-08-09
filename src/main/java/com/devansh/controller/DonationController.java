package com.devansh.controller;

import com.devansh.exception.UserException;
import com.devansh.model.User;
import com.devansh.request.DonationCreationRequest;
import com.devansh.response.DonationDto;
import com.devansh.response.PageableDonationDto;
import com.devansh.service.DonationService;
import com.devansh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/donation")
public class DonationController {

    private final DonationService donationService;
    private final UserService userService;

    // get filtered, sorted and paginated donations
    @GetMapping
    public ResponseEntity<PageableDonationDto> getAvailableDonations(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime beforeExpiry,
            @PageableDefault(size = 10, sort = "expiresAt", direction = Sort.Direction.ASC)Pageable pageable
            ) {
        return ResponseEntity.ok(donationService.getFilteredDonations(category, minQuantity, beforeExpiry, pageable));
    }

    @PostMapping
    public ResponseEntity<DonationDto> createDonation(@RequestBody DonationCreationRequest request,
                                                      @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findByJwtToken(token);
        return new ResponseEntity<>(donationService.createDonation(request, user), HttpStatus.CREATED);
    }

    @PutMapping("/{donationId}")
    public ResponseEntity<DonationDto> updateDonation(@RequestBody DonationCreationRequest request,
                                                      @RequestHeader("Authorization") String token,
                                                      @PathVariable Integer donationId) throws UserException {
        User user = userService.findByJwtToken(token);
        return new ResponseEntity<>(donationService.updateDonationById(donationId, request, user), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<DonationDto> getDonation(@PathVariable Integer donationId) {
        return new ResponseEntity<>(donationService.getDonationById(donationId), HttpStatus.OK);
    }

    @DeleteMapping("/{donationId}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Integer donationId) {
        donationService.deleteDonationById(donationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DonationDto>> getDonationByUserId(@PathVariable Integer userId) {
        return new ResponseEntity<>(donationService.getDonationsByUserId(userId), HttpStatus.OK);
    }



}


















