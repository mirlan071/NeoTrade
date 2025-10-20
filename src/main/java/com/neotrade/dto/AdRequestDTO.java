package com.neotrade.dto;

import com.neotrade.model.Category;
import com.neotrade.model.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdRequestDTO {
    @NotBlank(message = "{ad.title_required}")
    @Size(min = 3, max = 100, message = "{ad.title_size}")
    private String title;

    @NotBlank(message = "{ad.description_required}")
    @Size(min = 10, max = 1000, message = "{ad.description_size}")
    private String description;

    private Double price;

    private Category category;

    @NotNull(message = "{ad.region_required}")
    private Region region;
}