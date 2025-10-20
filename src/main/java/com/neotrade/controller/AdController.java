package com.neotrade.controller;

import com.neotrade.dto.AdRequestDTO;
import com.neotrade.dto.AdResponseDTO;
import com.neotrade.mapper.AdMapper;
import com.neotrade.model.Ad;
import com.neotrade.model.Category;
import com.neotrade.model.Region;
import com.neotrade.model.User;
import com.neotrade.repository.AdRepository;
import com.neotrade.repository.UserRepository;
import com.neotrade.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final MessageService messageService;

    // Создать объявление (только для продавцов)
    @PostMapping
    public ResponseEntity<?> createAd(
            @Valid @RequestBody AdRequestDTO adRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User owner = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

            // Проверяем, что пользователь - продавец
            if (!owner.isSeller()) {
                return ResponseEntity.status(403).body(
                        Map.of("error", messageService.getSellerRequiredError())
                );
            }

            Ad ad = adMapper.toEntity(adRequest);
            ad.setOwner(owner);
            ad.setCreatedAt(LocalDateTime.now());

            Ad savedAd = adRepository.save(ad);
            return ResponseEntity.ok(
                    Map.of(
                            "message", messageService.getAdCreated(),
                            "ad", adMapper.toDTO(savedAd)
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Получить все объявления (публичный доступ)
    @GetMapping
    public ResponseEntity<Page<AdResponseDTO>> getAllAds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "desc") String sort) {

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<Ad> ads = adRepository.findAllActiveAds(pageable);
        Page<AdResponseDTO> response = ads.map(adMapper::toDTO);

        return ResponseEntity.ok(response);
    }

    // Получить объявление по ID (публичный доступ)
    // Получить объявление по ID (публичный доступ)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAd(@PathVariable Long id) {
        Optional<Ad> ad = adRepository.findById(id);
        if (ad.isPresent()) {
            return ResponseEntity.ok(adMapper.toDTO(ad.get()));
        } else {
            return ResponseEntity.status(404).body(
                    Map.of("error", messageService.getAdNotFound())
            );
        }
    }

    // Получить объявления по региону (публичный доступ)
    @GetMapping("/region/{region}")
    public ResponseEntity<Page<AdResponseDTO>> getByRegion(
            @PathVariable Region region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort) {

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<Ad> ads = adRepository.findByRegion(region, pageable);
        Page<AdResponseDTO> response = ads.map(adMapper::toDTO);

        return ResponseEntity.ok(response);
    }

    // Получить мои объявления (только для авторизованных)
    @GetMapping("/my")
    public ResponseEntity<?> getMyAds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort) {

        try {
            User owner = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

            Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

            Page<Ad> ads = adRepository.findByOwner(owner, pageable);
            Page<AdResponseDTO> response = ads.map(adMapper::toDTO);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    // Расширенный поиск (публичный доступ)
    @GetMapping("/search")
    public ResponseEntity<Page<AdResponseDTO>> searchAds(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<Region> regions,
            @RequestParam(required = false) List<Category> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));

        Page<Ad> ads = adRepository.searchAdsWithFilters(query, regions, categories, minPrice, maxPrice, pageable);
        Page<AdResponseDTO> response = ads.map(adMapper::toDTO);

        return ResponseEntity.ok(response);
    }

    // Удалить объявление (только владелец)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Ad ad = adRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException(messageService.getAdNotFound()));

            User currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException(messageService.getUserNotFoundError()));

            if (!ad.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(
                        Map.of("error", messageService.getAdAccessDenied())
                );
            }

            adRepository.delete(ad);
            return ResponseEntity.ok(
                    Map.of("message", messageService.getOperationSuccess())
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}