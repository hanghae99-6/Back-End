package com.sparta.demo.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.demo.dto.user.NaverUserInfoDto;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {

    @Value("${naver.client_id}")
    String naverClientId;

    @Value("${naver.client_secret}")
    String naverSecret;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 네이버 로그인
    public void naverLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가코드" 로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        NaverUserInfoDto naverUserInfoDto = getNaverUserInfo(accessToken);

        // 3. 네이버ID로 회원가입 처리
        User NaverUser = signupNaverUser(naverUserInfoDto);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLoginNaverUser(NaverUser);

        // 5. response Header에 JWT 토큰 추가
        naverUsersAuthorizationInput(authentication, response);

//        try{
//            state = URLEncoder.encode("http://localhost:3000/user/naver/callback","UTF-8");
//        } catch (UnsupportedEncodingException e1){
//            e1.printStackTrace();
//            throw new RuntimeException("naverLogin 오류");
//        }
    }


    //header 에 Content-type 지정
    //1번
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        final String state = new BigInteger(130, new SecureRandom()).toString();
        System.out.println("getCode : " + code);

        //HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverSecret);
        body.add("redirect_uri", "http://localhost:3000/user/naver/callback");
        body.add("code", code);
        body.add("state", state);

        //HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
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
    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
//        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = jsonNode.get("response").get("id").asText();
        String nickname = jsonNode.get("response")
                .get("nickname").asText();
        String email = jsonNode.get("response")
                .get("email").asText();

        String profileImg = "";
        if(jsonNode.get("properties").get("profile_image") == null) {
            profileImg = null;
        } else {
            profileImg = jsonNode.get("properties").get("profile_image").asText();
        }
        log.info("카카오 사용자 정보 id: {},{},{},{}", id, nickname, profileImg, email);

        return new NaverUserInfoDto(id, nickname, profileImg, email);
    }

    // 3번
    private User signupNaverUser(NaverUserInfoDto naverUserInfo) {
        // DB 에 중복된 Naver Id 가 있는지 확인
        User findNaver = userRepository.findByEmail(naverUserInfo.getEmail()).orElse(null);

        Optional<User> findCheck = userRepository.findByEmail(naverUserInfo.getEmail());

        // 이미 가입된 사용자인지 확인
        if (findCheck.isPresent()){
            throw new NullPointerException("이미 가입된 아이디가 존재합니다.");
        }

        if (findNaver == null) {
            //회원가입
            String userName = naverUserInfo.getId();
            //username = naverNickname
            String nickName = naverUserInfo.getNickname();

            //password : random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            // email : naver email
            String email = naverUserInfo.getEmail();
            // profileImg
            String profileImg = naverUserInfo.getProfileImg();

            User user = User.builder()
                    .userName(userName)
                    .nickName(nickName)
                    .enPassword(encodedPassword)
                    .email(email)
                    .profileImg(profileImg)
                    .build();

            userRepository.save(user);

            return user;
        }
        return findNaver;
    }

    // 4번
    private Authentication forceLoginNaverUser(User naverUser) {
        UserDetails userDetails = new UserDetailsImpl(naverUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 5번
    private void naverUsersAuthorizationInput(Authentication authentication, HttpServletResponse response) {
        // response header에 token 추가
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        System.out.println("JWT토큰 : " + token);
        response.addHeader("Authorization", "BEARER" + " " + token);

    }
}