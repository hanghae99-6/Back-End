package com.sparta.demo.service;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.dto.debate.DebateRoomResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateService {

    private final DebateRepository debateRepository;


    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto) {
        Debate debate = Debate.create(debateLinkRequestDto);
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
}
