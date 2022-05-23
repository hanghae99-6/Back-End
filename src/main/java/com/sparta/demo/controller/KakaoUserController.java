package com.sparta.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.demo.service.user.KakaoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class KakaoUserController {
    private final KakaoUserService kakaoUserService;
    private final String AUTH_HEADER = "Authorization";

    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        kakaoUserService.kakaoLogin(code, response);
    }
}