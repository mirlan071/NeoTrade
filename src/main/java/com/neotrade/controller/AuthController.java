package com.neotrade.controller;

import com.neotrade.config.JwtUtil;
import com.neotrade.dto.*;
import com.neotrade.mapper.UserMapper;
import com.neotrade.model.User;
import com.neotrade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final com.neotrade.service.MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.ok(
                    Map.of("message", messageService.getRegistrationSuccess())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody SellerRegistrationRequestDTO request) {
        try {
            User user = userService.registerSeller(request);
            return ResponseEntity.ok(
                    Map.of("message", messageService.getMessage("seller.registration_success"))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        try {
            // Аутентификация через Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
            );

            User user = userService.authenticateUser(request);

            String accessToken = jwtUtil.generateToken(user.getPhoneNumber());

            AuthResponseDTO response = new AuthResponseDTO();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(3600L); // 1 час
            response.setUser(userMapper.toDTO(user));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", messageService.getLoginFailedError())
            );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", messageService.getMessage("registration.phone_required"))
                );
            }

            userService.requestPasswordReset(phone);
            return ResponseEntity.ok(
                    Map.of("message", messageService.getResetCodeSent())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        try {
            userService.resetPassword(request);
            return ResponseEntity.ok(
                    Map.of("message", messageService.getResetSuccess())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByPhone(userDetails.getUsername());
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}