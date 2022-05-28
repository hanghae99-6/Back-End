package com.sparta.demo.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoDto {

    private Long id;
    private String nickname;
    private String profileImg;
    private String email;

    public KakaoUserInfoDto(Long id, String nickname, String profileImg, String email){
        this.id = id;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.email = email;
    }

}
