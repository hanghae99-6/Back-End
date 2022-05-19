package com.sparta.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.demo.service.user.KakaoUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Controller
public class KakaoUserController {
    private final KakaoUserService kakaoUserService;
    private final String AUTH_HEADER = "Authorization";

    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("컨트롤러 입장 확인");
        System.out.println("code : " + code);

        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        String token = kakaoUserService.kakaoLogin(code);

        System.out.println("kakao token : " + token);
        response.addHeader(AUTH_HEADER, token);
    }

}