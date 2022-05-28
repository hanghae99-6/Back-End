package com.sparta.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    ALREADY_EXIST_WEEK_NAME(HttpStatus.BAD_REQUEST, "400_1", "중복된 주차가 존재합니다."),
    BLANK_WEEK_NAME(HttpStatus.BAD_REQUEST, "400_2", "주차는 필수 입력값입니다."),
    DISPLAY_WEEK_ID(HttpStatus.BAD_REQUEST, "400_3", "이 주차는 현재 display 중이며, 지울 수 없습니다."),

    TOO_LONG_WEEK_NAME(HttpStatus.BAD_REQUEST, "400_4", "주차 이름은 공백 포함 6글자 이내로 입력해주세요."),
    ALREADY_EXIST_TEAM_NAME(HttpStatus.BAD_REQUEST, "400_5", "중복된 팀 이름이 존재합니다."),
    SOLD_OUT_USER(HttpStatus.BAD_REQUEST, "400_6", "이미 다른팀에 소속되어 있습니다."),

    NO_MESSAGE(HttpStatus.BAD_REQUEST, "400_7", "메세지가 빈 값입니다."),

    BAD_FORM_TYPE(HttpStatus.BAD_REQUEST, "400_50", "이미지 형식이 올바르지 않습니다."),
    LIAR_USER_IMAGE(HttpStatus.BAD_REQUEST, "400_51", "업로드된 이미지가 존재하지 않습니다."),
    BAD_TOKEN(HttpStatus.BAD_REQUEST, "400_52", "유효하지 않은 토큰입니다."),
    TRY_START(HttpStatus.BAD_REQUEST, "400_53", "Start 를 먼저 눌러주세요."),
    NO_WEEK_MEMBER(HttpStatus.BAD_REQUEST, "400_54", "이 팀의 멤버가 아닙니다."),

    // 404 Not Found
    NOT_FOUND_USER_ID(HttpStatus.NOT_FOUND, "404_0", "유저 아이디가 존재하지 않습니다."),
    NOT_FOUND_DEBATE_ID(HttpStatus.NOT_FOUND, "404_1", "토론 방이 존재하지 않습니다."),

    NOT_FOUND_TEAM_ID(HttpStatus.NOT_FOUND, "404_2", "팀 아이디가 존재하지 않습니다."),
    NOT_FOUND_MEMBER_ID(HttpStatus.NOT_FOUND, "404_3", "멤버 아이디가 존재하지 않습니다."),
    NOT_FOUND_TEAM_IN_WEEK(HttpStatus.NOT_FOUND, "404_4", "팀 아이디가 해당 주차에 존재하지 않습니다."),

    NOT_EXIST_CHAT_FILE(HttpStatus.NOT_FOUND, "404_5", "마지막 채팅 내역입니다."),

    NO_WEEK(HttpStatus.NOT_FOUND, "404_50", "기본 주차가 존재하지 않습니다."),
    NOT_FOUND_TODO(HttpStatus.NOT_FOUND, "404_51", "할 일이 존재하지 않습니다."),


    // 500 Sever  Error
    FAIL_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "505_50", "파일 업로드에 실패 했습니다."),
    TRY_SUBSTRING(HttpStatus.INTERNAL_SERVER_ERROR, "505_51", "refreshToken 보낼 때, Bearer 붙여서 보내주는지 확인")
    ;


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

}
