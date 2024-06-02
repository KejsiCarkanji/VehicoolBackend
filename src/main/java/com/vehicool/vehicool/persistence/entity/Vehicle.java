package com.vehicool.vehicool.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(name="vin",nullable = false)
    private String vin;

    @Column(name="color",nullable = false)
    private String color;

    @Column(name="brand",nullable = false)
    private String Brand;

    @Column(name="model",nullable = false)
    private String model;

    @Column(name="engine_size",nullable = false)
    private Double engineSize;

    @Column(name="no_of_seats",nullable = false)
    private Long noOfSeats;

    @Column(name="plate_number",nullable = false)
    private String plateNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status")
    private DataPool status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location")
    private DataPool location;

    @Column(name="available",nullable = false)
    private Boolean available;

    @Column(name="production_year",nullable = false)
    private Long productionYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lender_id")
    @JsonIgnore
    private Lender lender;

    @OneToMany(fetch = FetchType.LAZY,mappedBy ="vehicle")
    @JsonIgnore
    private List<Contract> contractSigned;

    @OneToMany(mappedBy ="vehicle")
    @JsonIgnore
    private List<ConfidentialFile> vehicleRegistrations;

    @OneToMany(mappedBy ="vehicle")
    @JsonIgnore
    private List<FileData> images;

    @OneToOne(mappedBy ="vehicle")
    private VehicleCommerce vehicleCommerce;


    @ManyToOne(optional = false)
    @JoinColumn(name = "transmission_type")
    private DataPool transmissionType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "engine_type")
    private DataPool engineType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_type")
    private DataPool vehicleType;
}