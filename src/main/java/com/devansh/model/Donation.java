package com.devansh.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User donor;

    private String title;
    private Integer quantity;
    private String locationText;
    private LocalDateTime expiresAt;
    private String imageUrl;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private DonationStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private Category category;
}
