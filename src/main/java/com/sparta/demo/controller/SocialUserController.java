package com.sparta.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.service.user.KakaoService;
import com.sparta.demo.service.user.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SocialUserController {
    private final KakaoService kakaoService;
    private final NaverService naverService;

    //카카오 로그인
    @GetMapping("/kakao/callback")
    public KakaoUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response
    ) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    //네이버 로그인
    @GetMapping("/naver/callback")
    public void naverLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        naverService.naverLogin(code, response);
    }
}