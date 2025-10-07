package com.neotrade.mapper;

import com.neotrade.dto.AdResponseDto;
import com.neotrade.model.Ad;
import org.springframework.stereotype.Component;

@Component
public class AdMapper {


    public AdMapper toDto (Ad ad) {
        return new AdResponseDto(
                ad.getId(),
                ad.getTitle(),
                ad.getDescription(),
                ad.getPrice(),
                ad.getCategory().name(),
                ad.getRegion().name(),
                ad.getOwner().getFirstName() + " " + ad.getOwner().getLastName(),
                ad.getOwner().getPhoneNumber(),
                ad.getCreatedAt()
        );

    }
}
