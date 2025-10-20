package com.neotrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {
    @NotBlank(message = "{registration.phone_required}")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "{validation.phone_pattern}")
    private String phone;

    @NotBlank(message = "{password.reset_code_required}")
    @Size(min = 6, max = 6, message = "{validation.code_length}")
    private String code;

    @NotBlank(message = "{registration.password_required}")
    @Size(min = 6, message = "{registration.password_too_short}")
    private String newPassword;
}