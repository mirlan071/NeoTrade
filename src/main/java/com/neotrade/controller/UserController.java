package com.neotrade.controller;

import com.neotrade.dto.UserDTO;
import com.neotrade.mapper.UserMapper;
import com.neotrade.model.User;
import com.neotrade.repository.UserRepository;
import com.neotrade.service.MessageService;
import com.neotrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final MessageService messageService;

    // Получить профиль текущего пользователя
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Обновить профиль пользователя
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody User updatedUser) {
        try {
            User user = userService.updateUserProfile(userDetails.getUsername(), updatedUser);
            return ResponseEntity.ok(
                    Map.of(
                            "message", messageService.getOperationSuccess(),
                            "user", userMapper.toDTO(user)
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Получить пользователя по номеру телефона (публичный доступ)
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getUserByPhone(@PathVariable String phoneNumber) {
        try {
            User user = userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

            // Возвращаем только публичную информацию
            UserDTO publicUserDTO = UserDTO.builder()
                    .id(user.getId())
                    .phone(user.getPhoneNumber())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .companyName(user.getCompanyName())
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();

            return ResponseEntity.ok(publicUserDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Загрузка фото профиля
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", messageService.getMessage("file.empty_error"))
                );
            }

            // Проверяем тип файла
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", messageService.getMessage("file.image_only"))
                );
            }

            // Проверяем размер файла (максимум 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", messageService.getMessage("file.size_exceeded"))
                );
            }

            // Создаём директорию, если её нет
            Path uploadDir = Paths.get("uploads/users");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Уникальное имя файла
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID() + fileExtension;
            Path filePath = uploadDir.resolve(filename);

            // Копируем файл
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Сохраняем путь в БД
            String imageUrl = "/uploads/users/" + filename;
            userService.updateProfileImage(userDetails.getUsername(), imageUrl);

            return ResponseEntity.ok(
                    Map.of(
                            "message", messageService.getMessage("photo.upload_success"),
                            "imageUrl", imageUrl
                    )
            );

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", messageService.getMessage("file.upload_error"))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Удалить фото профиля
    @DeleteMapping("/photo")
    public ResponseEntity<?> deletePhoto(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

            // Если есть старое фото, пытаемся удалить файл
            if (user.getProfileImageUrl() != null) {
                try {
                    String oldImagePath = user.getProfileImageUrl().replace("/uploads/", "uploads/");
                    Path oldFilePath = Paths.get(oldImagePath);
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                } catch (IOException e) {
                    // Логируем ошибку, но не прерываем выполнение
                    System.err.println("Ошибка при удалении старого файла: " + e.getMessage());
                }
            }

            user.setProfileImageUrl(null);
            userRepository.save(user);

            return ResponseEntity.ok(
                    Map.of("message", messageService.getMessage("photo.delete_success"))
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}