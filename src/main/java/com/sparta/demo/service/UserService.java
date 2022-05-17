package com.sparta.demo.service;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.dto.user.MyReplyDto;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Likes;
import com.sparta.demo.model.Reply;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.repository.UserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final DebateVoteRepository debateVoteRepository;

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
        List<Debate> debate = debateRepository.findAllByProsNameOrConsName(userEmail, userEmail);

        List<MyDebateDto> myDebateDtoList = new ArrayList<>();

        for (int i = 0; i < debate.size(); i++) {
            List<Reply> replyList = replyRepository.findAllByDebate_DebateId(debate.get(i).getDebateId());
            int totalReply = replyList.size();
            Long totalCons = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.CONS, debate.get(i).getDebateId());
            Long totalPros = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.PROS, debate.get(i).getDebateId());

            MyDebateDto myDebateDto = new MyDebateDto(debate.get(i), totalPros, totalCons, totalReply);
            myDebateDtoList.add(myDebateDto);
        }

        return ResponseEntity.ok().body(myDebateDtoList);
    }

    @Transactional
    public ResponseEntity<List<MyReplyDto>> getMyReply(UserDetailsImpl userDetails) {
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());
        if (!user.isPresent()) {
            throw new IllegalArgumentException("유저정보가 없습니다");
        }
        String userEmail = user.get().getEmail();
        List<Reply> replyList = replyRepository.findAllByUser_Email(userEmail);

        List<MyReplyDto> myReplyDtoList = new ArrayList<>();

        for (int i = 0; i < replyList.size(); i++) {
            String reply = replyList.get(i).getReply();
            Debate debate = replyList.get(i).getDebate();
            String topic = debate.getTopic();
            CategoryEnum categoryEnum = debate.getCategoryEnum();
            String content = debate.getContent();
            List<Likes> likesList = replyList.get(i).getLikesList();

            MyReplyDto myReplyDto = new MyReplyDto(reply,likesList,topic,categoryEnum,content);
            myReplyDtoList.add(myReplyDto);
        }

        return ResponseEntity.ok().body(myReplyDtoList);
    }
}
