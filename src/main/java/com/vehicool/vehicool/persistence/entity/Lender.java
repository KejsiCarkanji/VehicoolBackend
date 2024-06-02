package com.vehicool.vehicool.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vehicool.vehicool.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "lender")
public class Lender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status")
    private DataPool status;

    @OneToMany(mappedBy ="lender")
    @JsonBackReference
    private List<Contract> contractSigned;

    @OneToMany(mappedBy ="lender")
    @JsonBackReference
    private List<RenterReview> reviewsGiven;

    @OneToMany(mappedBy ="lender")
    @JsonBackReference
    private List<LenderReview> reviewsRecieved;

    @OneToMany(mappedBy ="lender")
    @JsonBackReference
    private List<Vehicle> vehicles;
}
