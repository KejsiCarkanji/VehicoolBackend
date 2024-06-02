package com.vehicool.vehicool.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LenderReviewDTO {

    private String description;
    @NotNull(message = "Rating is required!")
    private Long rating;
}
