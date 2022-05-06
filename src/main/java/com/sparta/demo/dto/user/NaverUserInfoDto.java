package com.sparta.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NaverUserInfoDto {
    private String id;
    private String nickname;
    private String profileImg;
    private String email;
}
