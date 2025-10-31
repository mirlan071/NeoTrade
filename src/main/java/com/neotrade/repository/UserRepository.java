package com.neotrade.repository;

import com.neotrade.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Основной метод для поиска по номеру телефона
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Дополнительные методы
    Optional<User> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);

    // Старый метод (можно оставить для обратной совместимости)
    //Optional<User> findByUsername(String username);
}