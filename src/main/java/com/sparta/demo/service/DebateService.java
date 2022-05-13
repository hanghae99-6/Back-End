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

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebateService {

    private final DebateRepository debateRepository;
    private final EnterUserRepository enterUserRepository;


    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto, UserDetailsImpl userDetails) {

        log.info("userDetails.getUser().getUserName() : {}", userDetails.getUser().getUserName());

        Debate debate = Debate.create(debateLinkRequestDto, userDetails.getUser());
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

        log.info("prosName.isPresent(): {}",debate1.isPresent());
        log.info("consName.isPresent(): {}",debate2.isPresent());

        if(debate1.isPresent() || debate2.isPresent()) {
            debateRoomIdUserValidateDto.setUser(true);
        }


        Optional<Integer> found = enterUserRepository.countAllByDebate_RoomId(roomId);

        if(found.get()<2){
            log.info("found.get(): {}", found.get());
            debateRoomIdUserValidateDto.setSum(true);
            log.info("debate.get().getTopic(): {}",debate.get().getTopic());
            EnterUser enterUser = new EnterUser(debate.get(),username);
            enterUserRepository.save(enterUser);
        }

        return ResponseEntity.ok().body(debateRoomIdUserValidateDto);
    }


    public ResponseEntity<String> saveDebateInfo(String roomId, DebateInfoDto debateInfoDto, UserDetailsImpl userDetails) {
        String userName = userDetails.getUsername();
        Optional<Debate> optionalDebate = debateRepository.findByRoomId(roomId);
        Debate debate = optionalDebate.get();

        EnterUser enterUser = new EnterUser(debate, debateInfoDto, userName);
        enterUserRepository.save(enterUser);
        return ResponseEntity.ok().body("good!");
    }
}
