package com.neotrade.dto;

import com.neotrade.model.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAdRequest {

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 3, max = 100, message = "Заголовок должен быть от 3 до 100 символов")
    private String title;


    @Size(min = 10, max = 1000, message = "Описание должно быть от 10 до 1000 символов")
    private String description;

    @NotNull(message = "Цена обязательна")
    private Double price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Категория обязательна")
    private Category category;

    @NotBlank(message = "Пожалуйста выберите регион )")
    @Enumerated(EnumType.STRING)
    private String region;
}
