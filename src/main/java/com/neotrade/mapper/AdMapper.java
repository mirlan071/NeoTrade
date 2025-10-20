package com.neotrade.mapper;

import com.neotrade.dto.AdRequestDTO;
import com.neotrade.dto.AdResponseDTO;
import com.neotrade.model.Ad;
import org.springframework.stereotype.Component;

@Component
public class AdMapper {

    public AdResponseDTO toDTO(Ad ad) {
        if (ad == null) {
            return null;
        }

        UserMapper userMapper = new UserMapper();

        AdResponseDTO dto = new AdResponseDTO();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setCategory(ad.getCategory());
        dto.setRegion(ad.getRegion());
        dto.setOwner(userMapper.toDTO(ad.getOwner()));
        dto.setCreatedAt(ad.getCreatedAt());

        return dto;
    }

    public Ad toEntity(AdRequestDTO adRequest) {
        if (adRequest == null) {
            return null;
        }

        return Ad.builder()
                .title(adRequest.getTitle())
                .description(adRequest.getDescription())
                .price(adRequest.getPrice())
                .category(adRequest.getCategory())
                .region(adRequest.getRegion())
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}