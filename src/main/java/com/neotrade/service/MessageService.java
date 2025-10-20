package com.neotrade.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    public String getMessage(String code) {
        return getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            return code; // возвращаем код, если сообщение не найдено
        }
    }

    // Специфичные методы для удобства
    public String getRegistrationSuccess() {
        return getMessage("registration.success");
    }

    public String getPhoneExistsError() {
        return getMessage("registration.phone_exists");
    }

    public String getUserNotFoundError() {
        return getMessage("auth.user_not_found");
    }

    public String getLoginFailedError() {
        return getMessage("auth.login_failed");
    }

    public String getResetCodeSent() {
        return getMessage("password.reset_code_sent");
    }

    public String getResetCodeInvalid() {
        return getMessage("password.reset_code_invalid");
    }

    public String getResetSuccess() {
        return getMessage("password.reset_success");
    }

    public String getAttemptsExceededError() {
        return getMessage("password.reset_attempts_exceeded");
    }

    public String getSellerRequiredError() {
        return getMessage("role.seller_required");
    }

    public String getOperationSuccess() {
        return getMessage("operation.success");
    }

    public String getAdCreated() {
        return getMessage("ad.created");
    }

    public String getAdNotFound() {
        return getMessage("ad.not_found");
    }

    public String getAdAccessDenied() {
        return getMessage("ad.access_denied");
    }
}