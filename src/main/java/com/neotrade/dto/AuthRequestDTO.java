package com.neotrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @NotBlank(message = "{registration.phone_required}")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "{validation.phone_pattern}")
    private String phone;

    @NotBlank(message = "{registration.password_required}")
    @Size(min = 6, message = "{registration.password_too_short}")
    private String password;
}
