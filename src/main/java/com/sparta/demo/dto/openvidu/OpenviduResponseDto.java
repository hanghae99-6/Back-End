package com.sparta.demo.dto.openvidu;

import lombok.Data;

@Data
public class OpenviduResponseDto {
    private boolean ok;
    private String sessionName;
    private String token;
    private String nickName;
    private String userName;

    public OpenviduResponseDto(Boolean ok,String sessionName, String token, String clientData, Object loggerUser) {
        this.ok = ok;
        this.sessionName = sessionName;
        this.token = token;
        this.nickName = clientData;
        this.userName = (String) loggerUser;
    }

    public OpenviduResponseDto(boolean ok) {
        this.ok = ok;
    }

    public OpenviduResponseDto(Object logerUser) {
        this.userName = (String) logerUser;
    }
}
