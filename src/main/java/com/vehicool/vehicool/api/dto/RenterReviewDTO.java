package com.vehicool.vehicool.api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RenterReviewDTO {

    private String description;
    @NotNull(message = "Rating is required!")
    private Long rating;
}
