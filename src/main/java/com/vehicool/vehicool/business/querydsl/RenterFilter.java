package com.vehicool.vehicool.business.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RenterFilter {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}