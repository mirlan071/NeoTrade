package com.neotrade.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserProfileDto {

    private String firstName;
    private String lastName;
    private String email;
    private MultipartFile profileImage;
}
