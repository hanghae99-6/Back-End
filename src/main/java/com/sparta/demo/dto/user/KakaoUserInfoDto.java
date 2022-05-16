package com.sparta.demo.dto.user;

import com.sparta.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String profileImg;
    private String email;


    public KakaoUserInfoDto(Optional<User> user) {
    }
}
