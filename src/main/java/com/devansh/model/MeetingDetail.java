package com.devansh.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_details")
public class MeetingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Donation donation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User collector;

    private LocalDateTime meetingTime;
    private String meetingPlace;

    @Column(columnDefinition = "TEXT")
    private String notes;

}
