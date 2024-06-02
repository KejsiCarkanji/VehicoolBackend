package com.vehicool.vehicool.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusDTO {
    @NotNull(message = "Status is required!")
    private Long statusId;
}
