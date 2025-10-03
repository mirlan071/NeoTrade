package com.neotrade.service;



import com.neotrade.model.Ad;
import com.neotrade.model.User;
import com.neotrade.repository.AdRepository;
import com.neotrade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;

    public Ad createAd(Ad ad) {
        return adRepository.save(ad);
    }

    public List<Ad> getAllAds() {
        return adRepository.findAll();
    }

    public Ad getAdById(Long id) {
        return adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("не найдено"));
    }
    public User getUsername (String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));    }
}
