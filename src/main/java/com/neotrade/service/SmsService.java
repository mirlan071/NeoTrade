package com.neotrade.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final MessageService messageService;

    public void sendVerificationCode(String phone, String code) {
        String message = messageService.getMessage("sms.verification_code", new Object[]{code});
        sendSms(phone, message);
    }

    public void sendSellerVerificationCode(String phone, String code) {
        String message = messageService.getMessage("sms.seller_verification_code", new Object[]{code});
        sendSms(phone, message);
    }

    public void sendPasswordResetCode(String phone, String code) {
        String message = messageService.getMessage("sms.password_reset_code", new Object[]{code});
        sendSms(phone, message);
    }

    private void sendSms(String phone, String message) {
        // Реальная логика отправки SMS
        System.out.println("Sending SMS to " + phone + ": " + message);
        // Интеграция с SMS-сервисом (Twilio, SMS.ru и т.д.)
    }
}
