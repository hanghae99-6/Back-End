package com.sparta.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    NO_MESSAGE(HttpStatus.BAD_REQUEST, "400_1", "메세지가 빈 값입니다."),
    BAD_TOKEN(HttpStatus.BAD_REQUEST, "400_2", "유효하지 않은 토큰입니다."),
    TRY_START(HttpStatus.BAD_REQUEST, "400_3", "Start 를 먼저 눌러주세요."),

    // 404 Not Found
    NOT_FOUND_DEBATE_ID(HttpStatus.NOT_FOUND, "404_1", "토론 방이 존재하지 않습니다."),
    NOT_EXIST_CHAT_FILE(HttpStatus.NOT_FOUND, "404_2", "마지막 채팅 내역입니다."),

    // 토론방 EnterUser 확인
    ALREADY_EXIST_USER(HttpStatus.ALREADY_REPORTED, "500_1", "이미 존재하는 유저입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "500_2", "입장한 적 없는 유저입니다.")
    ;


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

}
