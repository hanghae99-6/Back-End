package com.sparta.demo.service;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.dto.user.MyReplyDto;
import com.sparta.demo.model.*;
import com.sparta.demo.repository.*;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DebateRepository debateRepository;
    private final ReplyRepository replyRepository;

    // 1. 닉네임 수정
    @Transactional
    public ResponseEntity<KakaoUserInfoDto> updateUserInfo(String nickName, UserDetailsImpl userDetails, HttpServletResponse response) {

        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        if (user.isPresent()) {
            user.get().setNickName(nickName);
            userRepository.save(user.get());
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다");
        }
        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto
                (user.get().getNickName(), user.get().getProfileImg(), user.get().getEmail());

        jwtTokenCreate(user, response);

        return ResponseEntity.ok().body(kakaoUserInfoDto);
    }

    private void jwtTokenCreate(Optional<User> kakaoUser,HttpServletResponse response) {
        String TOKEN_TYPE = "BEARER";

        UserDetails userDetails = new UserDetailsImpl(kakaoUser.get());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails1 = ((UserDetailsImpl) authentication.getPrincipal());

        System.out.println("userDetails1 : " + userDetails1.toString());

        final String token = JwtTokenUtils.generateJwtToken(userDetails1);

        System.out.println("JWT토큰 : " + token);
        response.addHeader("Authorization", TOKEN_TYPE + " " + token);
    }


    // 2. 프로필 페이지 - 토론 내역
    @Transactional
    public ResponseEntity<List<MyDebateDto>> getMyDebate(UserDetailsImpl userDetails) {
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        if (!user.isPresent()) {
            throw new IllegalArgumentException("유저정보가 없습니다");
        }
        String userEmail = user.get().getEmail();

//        List<Debate> debate = debateRepository.findAllByProsNameOrConsName(Sort.by(Sort.Direction.DESC, "createdAt"),userEmail, userEmail);
        List<Debate> debate = debateRepository.findTop60ByProsNameOrConsName(Sort.by(Sort.Direction.DESC, "debateId"),userEmail, userEmail);

        List<MyDebateDto> myDebateDtoList = new ArrayList<>();

        int side = 0;
        for (int i = 0; i < debate.size(); i++) {
            int totalReply = debate.get(i).getTotalReply();

            // 자신이 참여한 토론 중 자신이 찬성측인지 반대측인지. 찬성측이면 side =1 반대측이면 side =2.
            if(debate.get(i).getProsName().equals(user.get().getEmail())){
                side = 1 ;
            } else side = 2;

            MyDebateDto myDebateDto = new MyDebateDto(debate.get(i), totalReply, side);
            myDebateDtoList.add(myDebateDto);
        }

        return ResponseEntity.ok().body(myDebateDtoList);
    }

    // 3. 프로필 페이지 - 내가 쓴 댓글
    @Transactional
    public ResponseEntity<List<MyReplyDto>> getMyReply(UserDetailsImpl userDetails) {
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("유저정보가 없습니다");
        }
        String userEmail = user.get().getEmail();
//        List<Reply> replyList = replyRepository.findAllByUser_Email(Sort.by(Sort.Direction.DESC, "createdAt"), userEmail);
        List<Reply> replyList = replyRepository.findTop60ByUser_Email(Sort.by(Sort.Direction.DESC, "replyId"), userEmail);

        List<MyReplyDto> myReplyDtoList = new ArrayList<>();

        for (int i = 0; i < replyList.size(); i++) {
            Reply reply = replyList.get(i);
            Debate debate = replyList.get(i).getDebate();

            MyReplyDto myReplyDto = new MyReplyDto(reply, debate);
            myReplyDtoList.add(myReplyDto);
        }

        return ResponseEntity.ok().body(myReplyDtoList);
    }

    // 4. 프로필 페이지 - 나의 토론 내역 삭제
    @Transactional
    public void deleteMydebate(Long debateId, UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        debateRepository.deleteByDebateIdAndUser_Email(debateId, user.getEmail());
    }
}
