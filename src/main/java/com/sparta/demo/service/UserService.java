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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public ResponseEntity<KakaoUserInfoDto> updateUserInfo(String nickName, UserDetailsImpl userDetails) {
        log.info("nickName : {}", nickName);
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        if (user.isPresent()) {
            user.get().setNickName(nickName);
            log.info("UserService 44, user.get().getNickName() : {}", user.get().getNickName());
            userRepository.save(user.get());
        } else {
            throw new IllegalArgumentException("로그인 하지 않았습니다");
        }
        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto
                (user.get().getNickName(), user.get().getProfileImg(), user.get().getEmail());
        log.info("kakaoUserInfoDto.getNickname() : {}", kakaoUserInfoDto.getNickname());

        return ResponseEntity.ok().body(kakaoUserInfoDto);
    }

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
