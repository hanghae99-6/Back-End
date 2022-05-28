package com.sparta.demo.redis.chat.exception;

import lombok.Getter;

@Getter
public class CustomException extends IllegalArgumentException{

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
