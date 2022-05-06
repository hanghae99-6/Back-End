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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client_id}")
    String kakaoClientId;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public KakaoUserInfoDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가코드" 로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);
        System.out.println("인가 코드 : " + code);

        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getkakaoUserInfo(accessToken);
        System.out.println("엑세스 토큰 : " + accessToken);

        // 3. 카카오ID로 회원가입 처리
        User kakaoUser = signupkakaoUser(kakaoUserInfo);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLoginkakaoUser(kakaoUser);

        // 5. response Header에 JWT 토큰 추가
        kakaoUsersAuthorizationInput(authentication, response);
        return kakaoUserInfo;
    }


    //header 에 Content-type 지정
    //1번
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        System.out.println("getCode : " + code);

        //HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", "http://localhost:3000/user/kakao/callback");
        body.add("code", code);

        //HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //HTTP 응답 (JSON) -> 액세스 토큰 파싱
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    //2번
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
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String profileImg = "";
        if(jsonNode.get("properties").get("profile_image") == null) {
            profileImg = null;
        } else {
            profileImg = jsonNode.get("properties").get("profile_image").asText();
        }
        log.info("카카오 사용자 정보 id: {},{},{},{}", id, nickname, profileImg, email);
        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + profileImg + ", "+ email);

        return new KakaoUserInfoDto(id, nickname, profileImg, email);
    }

    // 3번
    private User signupkakaoUser(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 kakao Id 가 있는지 확인
//        User kakaoUser = userRepository.findByUserName(Long.toString(kakaoUserInfo.getId())).orElse(null);
        User kakaoUser = userRepository.findByEmail(kakaoUserInfo.getEmail()).orElse(null);

        Optional<User> findCheck = userRepository.findByEmail(kakaoUserInfo.getEmail());

        // 이미 가입된 사용자인지 확인
        if (findCheck.isPresent()){
            throw new NullPointerException("이미 가입된 아이디가 존재합니다.");
        }

        if (kakaoUser == null) {
            //회원가입
            String userName = String.valueOf(kakaoUserInfo.getId());
            //nickName = kakaoNickname
            String nickName = kakaoUserInfo.getNickname();
            //password : random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            // email : kakao email
            String email = kakaoUserInfo.getEmail();
            // profileImg
            String profileImg = kakaoUserInfo.getProfileImg();

            User user = User.builder()
                    .userName(userName)
                    .nickName(nickName)
                    .email(email)
                    .enPassword(encodedPassword)
                    .profileImg(profileImg)
                    .build();

            userRepository.save(user);
            return user;

        }
        return kakaoUser;
    }

    // 4번
    private Authentication forceLoginkakaoUser(User kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 5번
    private void kakaoUsersAuthorizationInput(Authentication authentication, HttpServletResponse response) {
        // response header에 token 추가
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        System.out.println("JWT토큰 : " + token);
        response.addHeader("Authorization", "BEARER" + " " + token);
    }
}