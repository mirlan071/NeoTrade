package com.neotrade.controller;

import com.neotrade.model.User;
import com.neotrade.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Getter("/phoneNumber")
    re

    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Создаём директорию, если её нет
        Path uploadDir = Paths.get("uploads/users");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Уникальное имя файла
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Сохраняем путь в БД
        user.setProfileImageUrl("/uploads/users/" + filename);
        userRepository.save(user);

        return ResponseEntity.ok("Фото успешно загружено");
    }
}

