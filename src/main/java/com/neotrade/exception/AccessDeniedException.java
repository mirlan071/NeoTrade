package com.neotrade.exception;

public class AccessDeniedException extends BaseException {
    public AccessDeniedException() {
        super("error.access_denied");
    }

    public AccessDeniedException(String messageCode) {
        super(messageCode);
    }
}