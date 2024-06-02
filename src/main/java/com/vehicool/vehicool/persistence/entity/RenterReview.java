package com.vehicool.vehicool.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "renter_review")
public class RenterReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="description")
    private String description;

    @Column(name="rating",nullable = false)
    private Long rating;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lender_id")
    private Lender lender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "renter_id")
    private Renter renter;
}
