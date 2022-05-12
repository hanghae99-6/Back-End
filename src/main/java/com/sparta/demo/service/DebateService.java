package com.sparta.demo.service;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.model.Debate;
import com.sparta.demo.repository.DebateRepository;
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

    public ResponseEntity<DebateRoomValidateDto> checkRoomIdUser(DebateRoomIdUserCheckDto debateRoomIdUserCheckDto) {

        Optional<Debate> debate = debateRepository.findByRoomId(debateRoomIdUserCheckDto.getRoomId());
        DebateRoomValidateDto debateRoomValidateDto = new DebateRoomValidateDto();
        log.info("debate.isPresent(): {}",debate.isPresent());
        debateRoomValidateDto.setRoomId(debate.isPresent());

        Optional<Debate> debate1 = debateRepository.findByRoomIdAndProsName(debateRoomIdUserCheckDto.getRoomId(),debateRoomIdUserCheckDto.getUsername());
        Optional<Debate> debate2 = debateRepository.findByRoomIdAndConsName(debateRoomIdUserCheckDto.getRoomId(), debateRoomIdUserCheckDto.getUsername());

        log.info("debate1.isPresent(): {}",debate1.isPresent());
        log.info("debate2.isPresent(): {}",debate2.isPresent());

        if(debate1.isPresent() || debate2.isPresent()) {
            debateRoomValidateDto.setUser(true);
        }


        return ResponseEntity.ok().body(debateRoomValidateDto);
    }


}
