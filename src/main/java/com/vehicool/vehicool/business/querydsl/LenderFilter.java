package com.vehicool.vehicool.business.querydsl;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LenderFilter {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}