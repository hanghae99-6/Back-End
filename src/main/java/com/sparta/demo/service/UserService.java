package com.sparta.demo.service;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.UserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DebateRepository debateRepository;

    @Transactional
    public KakaoUserInfoDto updateUserInfo(String nickName, UserDetailsImpl userDetails){

        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        if(nickName == null)
        nickName = user.get().getNickName();

        if(user.isPresent()){
            user.get().update(nickName);
            return new KakaoUserInfoDto(user);
        } else{
            throw new IllegalArgumentException("로그인 하지 않았습니다");
        }

    }

    @Transactional
    public ResponseEntity<MyDebateDto> getMyDebate(UserDetailsImpl userDetails){
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        Optional<Debate> debate = debateRepository.findByProsNameOrConsName(user.get().getEmail());
    }
}
