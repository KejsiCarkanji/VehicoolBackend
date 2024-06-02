package com.vehicool.vehicool.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vehicle_review")
public class VehicleReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="description")
    private String description;

    @Column(name="rating",nullable = false)
    private Long rating;

    @ManyToOne(optional = false)
    @JoinColumn(name="vehicle_id")
    private Vehicle vehicleReviewed;

    @ManyToOne(optional = false)
    @JoinColumn(name="renter_id")
    private Renter renter;
}
