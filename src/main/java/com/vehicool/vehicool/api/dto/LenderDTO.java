package com.vehicool.vehicool.api.dto;

import com.vehicool.vehicool.persistence.entity.Contract;
import com.vehicool.vehicool.persistence.entity.LenderReview;
import com.vehicool.vehicool.persistence.entity.RenterReview;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LenderDTO {

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
