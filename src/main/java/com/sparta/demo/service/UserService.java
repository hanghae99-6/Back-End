package com.sparta.demo.service;

import com.sparta.demo.dto.user.KakaoUserInfoDto;
import com.sparta.demo.dto.user.MyDebateDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import com.sparta.demo.model.User;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.repository.UserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DebateRepository debateRepository;
    private final ReplyRepository replyRepository;
    private final DebateVoteRepository debateVoteRepository;

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
    public ResponseEntity<List<MyDebateDto>> getMyDebate(UserDetailsImpl userDetails){
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());

        List<Debate> debate = debateRepository.findAllByProsNameOrConsName(user.get().getEmail());

        List<MyDebateDto> myDebateDtoList = new ManagedList<>();

        for(int i=0; i<debate.size();i++){
            List<Reply> replyList = replyRepository.findAllByDebate_DebateId(debate.get(i).getDebateId());
            int totalReply = replyList.size();
            Long totalCons = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.CONS, debate.get(i).getDebateId());
            Long totalPros = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.PROS, debate.get(i).getDebateId());

            MyDebateDto myDebateDto = new MyDebateDto(debate.get(i), totalPros, totalCons, totalReply);
            myDebateDtoList.add(myDebateDto);
        }

        return ResponseEntity.ok().body(myDebateDtoList);
    }
}
