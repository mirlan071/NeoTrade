package com.neotrade.mapper;

import com.neotrade.dto.UserDTO;
import com.neotrade.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setPhone(user.getPhoneNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setCompanyName(user.getCompanyName());
        dto.setContactPerson(user.getContactPerson());

        return dto;
    }

    public User toEntity(com.neotrade.dto.AuthRequestDTO authRequest) {
        if (authRequest == null) {
            return null;
        }

        return User.builder()
                .phoneNumber(authRequest.getPhone())
                .password(authRequest.getPassword()) // будет закодирован в сервисе
                .firstName("") // обязательные поля
                .lastName("")
                .role(com.neotrade.model.UserRole.USER)
                .build();
    }
}