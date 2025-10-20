package com.neotrade.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Телефон используется как логин
    @NotBlank(message = "{registration.phone_required}")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "{validation.phone_pattern}")
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @NotBlank(message = "{registration.password_required}")
    private String password;

    @NotBlank(message = "{user.firstname_required}")
    private String firstName;

    @NotBlank(message = "{user.lastname_required}")
    private String lastName;

    @Email(message = "{user.email_invalid}")
    @Column(unique = true)
    private String email;

    // Заменяем Set<String> roles на одну роль
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // Дополнительные поля для продавцов
    private String companyName;
    private String contactPerson;

    // Статусы
    private boolean enabled = true;
    private boolean phoneVerified = false;

    // Даты
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLogin;

    public boolean isSeller() {
        return UserRole.SELLER.equals(this.role);
    }
}