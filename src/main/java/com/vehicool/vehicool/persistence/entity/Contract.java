package com.vehicool.vehicool.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="start_date",nullable = false)
    Date startDate;

    @Column(name="end_date",nullable = false)
    Date endDate;

    @Column(name="price_per_day",nullable = false)
    Double pricePerDay;

    @Column(name="total",nullable = false)
    Double total;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lender_id",nullable = false)
    private Lender lender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id",nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "renter_id",nullable = false)
    private Renter renter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contractual_status",nullable = false)
    private DataPool contractualStatus;

}
