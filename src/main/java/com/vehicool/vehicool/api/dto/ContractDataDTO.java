package com.vehicool.vehicool.api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ContractDataDTO {
    @NotNull(message = "Start date is required!")
    private Date startDate;

    @NotNull(message = "End date is required!")
    private Date endDate;
}
