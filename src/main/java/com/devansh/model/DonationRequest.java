package com.devansh.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation_requests")
public class DonationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Donation donation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User requester;

    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

}
