package com.sparta.demo.service;

import com.sparta.demo.dto.live.LiveResponseDto;
import com.sparta.demo.enumeration.StatusTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveNowService {
    private final DebateRepository debateRepository;
    public ResponseEntity<List<LiveResponseDto>> getLiveNow() {

        List<Debate> debateList = debateRepository.findAllByStatusEnumOrStatusEnum(StatusTypeEnum.LIVEON, StatusTypeEnum.HOLD);
        List<LiveResponseDto> liveResponseDtoList = new ArrayList<>();

        for (Debate debate: debateList) {
            liveResponseDtoList.add(new LiveResponseDto(debate));
        }

        return ResponseEntity.ok().body(liveResponseDtoList);
    }
}
