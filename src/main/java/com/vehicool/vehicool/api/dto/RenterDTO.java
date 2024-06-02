package com.vehicool.vehicool.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenterDTO {

    @NotNull(message = "First name is required!")
    private String firstName;

    @NotNull(message = "Last name is required!")
    private String lastName;

    @NotNull(message = "Age is required!")
    private Integer age;

    @NotNull(message = "Email is required!")
    private String email;

    @NotNull(message = "Phone number is required!")
    private String phoneNumber;

}
