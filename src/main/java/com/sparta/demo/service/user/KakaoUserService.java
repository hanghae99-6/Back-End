package com.sparta.demo.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.UserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoUserService {

    @Value("${kakao.client_id}")
    String kakaoClientId;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        System.out.println("인가 코드 : " + code);
        log.info("엑세스 토큰: {}", accessToken);

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getkakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User kakaoUser = registerkakaoUserIfNeeded(kakaoUserInfo);

        // 4. 로그인 JWT 토큰 발행
        jwtTokenCreate(kakaoUser, response);
    }


    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        // 클라이언트 아이디, url 확인
        body.add("client_id", kakaoClientId);

//        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("redirect_uri", "https://www.wepeech.com/user/kakao/callback");
//        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");


        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 카카오에서 동의 항목 가져오기
    private KakaoUserInfoDto getkakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
//        String email = jsonNode.get("kakao_account")
//                .get("email").asText();
//        String profileImg = jsonNode.get("properties").get("profile_image").asText();
        String profileImg = "";
        if(jsonNode.get("properties").get("profile_image") == null) {
            profileImg = "null";
        } else {
            profileImg = jsonNode.get("properties").get("profile_image").asText();
        }
        String email = jsonNode.get("kakao_account").get("email").asText();
        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + profileImg + ", "+ email);

        return new KakaoUserInfoDto(id, nickname, profileImg, email);
    }

    private User registerkakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElse(null);
        if (kakaoUser == null) {
            // 카카오 사용자 이메일과 동일한 이메일을 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser.setId(kakaoId);
            } else {
                // 신규 회원가입
                String kakaoUsername = Long.toString(kakaoId);
                // username: kakao nickname
                String kakaoNickname = kakaoUserInfo.getNickname();
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);
                String profileImg = kakaoUserInfo.getProfileImg();

                // email: kakao email
                String email = kakaoUserInfo.getEmail();
                // role: 일반 사용자

                kakaoUser = new User(kakaoUsername, kakaoNickname, encodedPassword, email, profileImg);
            }

            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void jwtTokenCreate(User kakaoUser, HttpServletResponse response) {
        String TOKEN_TYPE = "BEARER";

        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());

        System.out.println("userDetails1 : " + userDetails1.toString());

        final String token = JwtTokenUtils.generateJwtToken(userDetails1);

        System.out.println("token값:"+ token);
        response.addHeader("Authorization", "BEARER" + " " + token);
//        return TOKEN_TYPE + " " + token;
    }
}
