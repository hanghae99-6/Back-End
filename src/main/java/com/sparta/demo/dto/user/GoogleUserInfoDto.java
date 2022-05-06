package com.sparta.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUserInfoDto {
    private String id;
    private String nickname;
    private String profileImg;
    private String email;
}
