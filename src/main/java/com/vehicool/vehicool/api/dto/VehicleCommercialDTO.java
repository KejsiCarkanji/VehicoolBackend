package com.vehicool.vehicool.api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class VehicleCommercialDTO {
    @NotNull(message = "Price per day is required!")
    private Double pricePerDay;

    @NotNull(message = "Date available is required!")
    private Date dateAvailable;

    @NotNull(message = "Max date available is required!")
    private Date maxDateAvailable;

    @NotNull(message = "Max date available is required!")
    private Boolean isAvailable;
}
