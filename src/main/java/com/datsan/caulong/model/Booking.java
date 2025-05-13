package com.datsan.caulong.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Court court;

    @ManyToOne
    private Type type;

    private String status;
    private String linkGroup;
    private String note;
    private double total;
    private LocalDateTime bookingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "booking")
    private List<EventBooking> eventBookings;
}

