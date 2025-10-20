package com.neotrade.dto;

import com.neotrade.model.Category;
import com.neotrade.model.Region;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Category category;
    private Region region;
    private UserDTO owner;
    private LocalDateTime createdAt;

    public static AdResponseDTO fromAd(com.neotrade.model.Ad ad) {
        AdResponseDTO dto = new AdResponseDTO();
        dto.setId(ad.getId());
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setCategory(ad.getCategory());
        dto.setRegion(ad.getRegion());
        dto.setOwner(UserDTO.fromUser(ad.getOwner()));
        dto.setCreatedAt(ad.getCreatedAt());
        return dto;
    }
}