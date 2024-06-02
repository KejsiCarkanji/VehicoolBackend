package com.vehicool.vehicool.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class AppealDTO {
    @NotNull
    private String description;
    private List<MultipartFile> supportFiles;
}
