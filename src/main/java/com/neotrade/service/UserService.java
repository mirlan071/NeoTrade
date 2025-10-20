package com.neotrade.service;

import com.neotrade.dto.AuthRequestDTO;
import com.neotrade.dto.PasswordResetRequestDTO;
import com.neotrade.dto.SellerRegistrationRequestDTO;
import com.neotrade.model.*;
import com.neotrade.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final MessageService messageService;

    public User registerUser(AuthRequestDTO request) {
        if (userRepository.findByPhoneNumber(request.getPhone()).isPresent()) {
            throw new RuntimeException(messageService.getPhoneExistsError());
        }

        User user = User.builder()
                .phoneNumber(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName("") // обязательные поля
                .lastName("")
                .role(UserRole.USER)
                .phoneVerified(false)
                .enabled(true)
                .build();

        String verificationCode = generateRandomCode();
        smsService.sendVerificationCode(request.getPhone(), verificationCode);

        return userRepository.save(user);
    }

    public User registerSeller(SellerRegistrationRequestDTO request) {
        if (userRepository.findByPhoneNumber(request.getPhone()).isPresent()) {
            throw new RuntimeException(messageService.getPhoneExistsError());
        }

        User user = User.builder()
                .phoneNumber(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getContactPerson() != null ? request.getContactPerson() : "")
                .lastName("")
                .email(request.getEmail())
                .role(UserRole.SELLER)
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .phoneVerified(false)
                .enabled(true)
                .build();

        String verificationCode = generateRandomCode();
        smsService.sendSellerVerificationCode(request.getPhone(), verificationCode);

        return userRepository.save(user);
    }

    public User authenticateUser(AuthRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhone())
                .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException(messageService.getLoginFailedError());
        }

        if (!user.isEnabled()) {
            throw new RuntimeException(messageService.getMessage("auth.account_disabled"));
        }

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void validateSellerRole(User user) {
        if (!user.isSeller()) {
            throw new RuntimeException(messageService.getSellerRequiredError());
        }
    }

    public void requestPasswordReset(String phone) {
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

        String resetCode = generateRandomCode();
        // TODO: Сохранить код в базу для проверки

        smsService.sendPasswordResetCode(phone, resetCode);
    }

    public void resetPassword(PasswordResetRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhone())
                .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

        // TODO: Проверить код из базы
        // if (!isValidResetCode(request.getPhone(), request.getCode())) {
        //     throw new RuntimeException(messageService.getResetCodeInvalid());
        // }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // Добавляем эти методы в UserService

    public User getUserByPhone(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));
    }

    public User updateUserProfile(String phoneNumber, User updatedUser) {
        User existingUser = getUserByPhone(phoneNumber);

        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getCompanyName() != null) {
            existingUser.setCompanyName(updatedUser.getCompanyName());
        }
        if (updatedUser.getContactPerson() != null) {
            existingUser.setContactPerson(updatedUser.getContactPerson());
        }

        return userRepository.save(existingUser);
    }

    public void updateProfileImage(String phoneNumber, String imageUrl) {
        User user = getUserByPhone(phoneNumber);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }
}