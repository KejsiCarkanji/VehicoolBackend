package com.vehicool.vehicool.api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleDTO {

    @NotNull(message = "VIN is required!")
    private String vin;

    @NotNull(message = "Color is required!")
    private String color;

    @NotNull(message = "Brand is required!")
    private String Brand;

    @NotNull(message = "Model is required!")
    private String model;

    @NotNull(message = "PlateNo is required!")
    private String plateNo;

    @NotNull(message = "Vehicle type Id is required!")
    private Long vehicleTypeId;

    @NotNull(message = "Transmission Type Id is required!")
    private Long transmissionTypeId;

    @NotNull(message = "Engine Type Id is required!")
    private Long engineTypeId;

    @NotNull(message = "Vat ID is required!")
    private Double engineSize;

    @NotNull(message = "Number is required!")
    private Long noOfSeats;

    @NotNull(message = "Production Year is required!")
    private Long productionYear;

    @NotNull(message = "City is required!")
    private Long cityId;

}