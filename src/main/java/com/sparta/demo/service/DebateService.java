package com.sparta.demo.service;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.EnterUserRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebateService {

    private final DebateRepository debateRepository;
    private final EnterUserRepository enterUserRepository;


    // todo : 프론트에서 로그인 기능까지 합칠 경우
//    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto, UserDetailsImpl userDetails) {
//
//        log.info("userDetails.getUser().getUserName() : {}", userDetails.getUser().getUserName());
//
//        Debate debate = Debate.create(debateLinkRequestDto, userDetails.getUser());
//        Debate newDebate = debateRepository.save(debate);
//
//        DebateLinkResponseDto debateLinkResponseDto = new DebateLinkResponseDto();
//        debateLinkResponseDto.setRoomId(newDebate.getRoomId());
//
//        return ResponseEntity.ok().body(debateLinkResponseDto);
//    }

    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto) {

        log.info("debateLinkRequestDto.getSpeechMinute(): {}",debateLinkRequestDto.getSpeechMinute());
        log.info("debateLinkRequestDto.getContent(): {}",debateLinkRequestDto.getContent());
        log.info("userDetails.getUser().getUserName() : {}", "유저디테일즈 안씀");

        Debate debate = Debate.create(debateLinkRequestDto, null);
        Debate newDebate = debateRepository.save(debate);

        DebateLinkResponseDto debateLinkResponseDto = new DebateLinkResponseDto();
        debateLinkResponseDto.setRoomId(newDebate.getRoomId());

        return ResponseEntity.ok().body(debateLinkResponseDto);
    }

    public ResponseEntity<DebateRoomResponseDto> getRoom(String roomId) {
        Debate debate = debateRepository.findByRoomId(roomId).orElseThrow(()->new NullPointerException("존재하지 않는 방입니다."));
        return ResponseEntity.ok().body(new DebateRoomResponseDto(debate));
    }





    // StompHandler에서 필요한 메서드 모음(삭제하거나 이동 예정)
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(String roomId, String username) {

        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        DebateRoomIdUserValidateDto debateRoomIdUserValidateDto = new DebateRoomIdUserValidateDto();
        log.info("debate.isPresent(): {}",debate.isPresent());
        debateRoomIdUserValidateDto.setRoomId(debate.isPresent());

        Optional<Debate> debate1 = debateRepository.findByRoomIdAndProsName(roomId, username);
        Optional<Debate> debate2 = debateRepository.findByRoomIdAndConsName(roomId, username);

        log.info("debate1.isPresent(): {}",debate1.isPresent());
        log.info("debate2.isPresent(): {}",debate2.isPresent());

        if(debate1.isPresent() || debate2.isPresent()) {
            debateRoomIdUserValidateDto.setUser(true);
        }

        EnterUser enterUser = new EnterUser(debate.get(),username);
        enterUserRepository.save(enterUser);

        Optional<Integer> found = enterUserRepository.countAllByDebate_RoomId(roomId);

        if(found.get()<2){
            log.info("found.get(): {}", found.get());
            debateRoomIdUserValidateDto.setSum(true);
        }

        return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
    }
    public ResponseEntity<DebateUserValidateDto> checkUser(DebateUserCheckDto debateUserCheckDto) {

//        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
//        DebateRoomIdValidateDto debateRoomIdValidateDto = new DebateRoomIdValidateDto();
//        log.info("debate.isPresent(): {}",debate.isPresent());
//        debateRoomIdValidateDto.setRoomId(debate.isPresent());

        DebateUserValidateDto debateUserValidateDto = new DebateUserValidateDto();

        Optional<Debate> debate1 = debateRepository.findByRoomIdAndProsName(debateUserCheckDto.getRoomId(),debateUserCheckDto.getUsername());
        Optional<Debate> debate2 = debateRepository.findByRoomIdAndConsName(debateUserCheckDto.getRoomId(), debateUserCheckDto.getUsername());

        log.info("debate1.isPresent(): {}",debate1.isPresent());
        log.info("debate2.isPresent(): {}",debate2.isPresent());

        if(debate1.isPresent() || debate2.isPresent()) {
            debateUserValidateDto.setUser(true);
        }


        return ResponseEntity.ok().body(debateUserValidateDto);
    }


}
