package com.sparta.demo.service;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.dto.debate.DebateRoomResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateService {

    private final DebateRepository debateRepository;



    public DebateLinkResponseDto createLink(DebateLinkRequestDto debateLinkRequestDto) {
        Debate debate = Debate.create(debateLinkRequestDto);
        Debate newDebate = debateRepository.save(debate);

        DebateLinkResponseDto debateLinkResponseDto = new DebateLinkResponseDto();
        debateLinkResponseDto.setRoomId(newDebate.getRoomId());

        return debateLinkResponseDto;
    }

    public DebateRoomResponseDto getRoom(String roomId) {
        Debate debate = debateRepository.findByroomId(roomId);
        return new DebateRoomResponseDto(debate);
    }
}
