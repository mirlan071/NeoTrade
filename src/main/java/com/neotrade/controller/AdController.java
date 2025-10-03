package com.neotrade.controller;

import com.neotrade.model.Ad;
import com.neotrade.model.Category;
import com.neotrade.model.Region;
import com.neotrade.model.User;
import com.neotrade.repository.AdRepository;
import com.neotrade.repository.UserRepository;
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

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdRepository adRepository;
    private final UserRepository userRepository;

    // Создать объявление (только авторизованный)
    @PostMapping
    public Ad createAd(@RequestBody Ad ad, @AuthenticationPrincipal UserDetails userDetails) {
        User owner = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ad.setOwner(owner);
        ad.setCreatedAt(LocalDateTime.now());

        // регион должен быть передан в теле JSON как строка, например: "ALMATY"
        if (ad.getRegion() == null) {
            throw new RuntimeException("Регион обязателен для указания");
        }

        return adRepository.save(ad);
    }


    //Получить все объявления
    @GetMapping
    public Page <Ad> getAllAds(Pageable pageable) {
        return adRepository.findAllAdc(pageable);
    }

    @GetMapping("/region/{region}")
    public Page<Ad> getByRegion(
            @PathVariable Region region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
        return adRepository.findByRegion(region, pageable);
    }


    //Получить объявления текущего пользователя
    @GetMapping("/my")
    public Page <Ad> getMyAds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        User owner = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                sortOrder.equalsIgnoreCase("asc")
                        ? Sort.by("createdAt").ascending()
                        : Sort.by("createdAt").descending()
        );

        return adRepository.findByOwner(owner, pageable);
    }



    @GetMapping("/search")
    public Page<Ad> searchAds(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<Region> regions,
            @RequestParam(required = false) List<Category> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                sortOrder.equalsIgnoreCase("asc")
                        ? Sort.by("createdAt").ascending()
                        : Sort.by("createdAt").descending()
        );

        if (query != null && regions != null) {
            return adRepository.findByTitleContainingIgnoreCaseAndRegionIn(query, regions, pageable);
        } else if (query != null) {
            return adRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else if (categories != null && regions != null && minPrice != null && maxPrice != null) {
            return adRepository.findByCategoryInAndPriceBetweenAndRegionIn(categories, minPrice, maxPrice, regions, pageable);
        } else if (regions != null && minPrice != null && maxPrice != null) {
            return adRepository.findByPriceBetweenAndRegionIn(minPrice, maxPrice, regions, pageable);
        } else if (regions != null) {
            return adRepository.findByRegionIn(regions, pageable);
        } else {
            return adRepository.findAll(pageable);
        }
    }


    // Удалить объявление (только владелец)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAd(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (!ad.getOwner().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Вы не владелец этого объявления");
        }

        adRepository.delete(ad);
        return ResponseEntity.ok("Объявление удалено");
    }
}
