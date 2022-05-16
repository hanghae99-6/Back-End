package com.sparta.demo.controller;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public void helloWorld() {
        log.info("접속 : {}", "로그");
    }

    // 닉네임 변경
    @PutMapping("/nick-name")
    public KakaoUserInfoDto updateUserInfo(@RequestParam String nickName, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.updateUserInfo(nickName, userDetails);
    }

    // 프로필 - 1. 토론내역
    @GetMapping("/mydebate")
    public ResponseEntity<MyDebateDto> getMyDebate(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.getMyDebate(userDetails);
    }
}
