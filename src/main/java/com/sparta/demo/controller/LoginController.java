package com.sparta.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.demo.service.user.KakaoUserService;
import com.sparta.demo.service.user.NaverService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Api(tags = {"Social Login (Kakao)"})
@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final KakaoUserService kakaoUserService;
    private final NaverService naverService;
    private final String AUTH_HEADER = "Authorization";

    // 카카오 로그인
    @ApiOperation(value = "카카오 소셜 로그인", notes = "카카오 소셜 로그인")
    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        kakaoUserService.kakaoLogin(code, response);
    }

    // 네이버 로그인
    @ApiOperation(value = "네이버 소셜 로그인", notes = "네이버 소셜 로그인")
    @GetMapping("/naver/callback")
    public void naverLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        naverService.naverLogin(code, response);
    }
}