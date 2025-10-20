package com.neotrade.exception;

public abstract class BaseException extends RuntimeException {
    private final String messageCode;
    private final Object[] args;

    public BaseException(String messageCode) {
        this(messageCode, null, null);
    }

    public BaseException(String messageCode, Object[] args) {
        this(messageCode, args, null);
    }

    public BaseException(String messageCode, Object[] args, Throwable cause) {
        super(messageCode, cause);
        this.messageCode = messageCode;
        this.args = args;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getArgs() {
        return args;
    }
}