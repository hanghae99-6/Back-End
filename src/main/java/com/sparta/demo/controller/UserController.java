package com.sparta.demo.controller;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public void helloWorld() {
        log.info("접속 : {}", "로그");
    }

    @PutMapping("/user/update-info")
    public KakaoUserInfoDto updateUserInfo(@RequestParam String nickName, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.updateUserInfo(nickName, userDetails);
    }
}
