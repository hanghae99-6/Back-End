package com.sparta.demo.service;

import com.sparta.demo.dto.live.LiveResponseDto;
import com.sparta.demo.enumeration.StatusTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.User;
import com.sparta.demo.redis.chat.repository.ChatMessageRepository;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveNowService {

    private final DebateRepository debateRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ResponseEntity<List<LiveResponseDto>> getLiveNow() {

        List<Debate> debateList = debateRepository.findAllByStatusEnumOrStatusEnum(StatusTypeEnum.LIVEON, StatusTypeEnum.HOLD);
        List<LiveResponseDto> liveResponseDtoList = new ArrayList<>();

        log.info("debateList.size(): {}", debateList.size());

        for (Debate debate: debateList) {
            Optional<User> prosUser = userRepository.findByEmail(debate.getProsName());
            Optional<User> consUser = userRepository.findByEmail(debate.getConsName());
            Long userCnt = chatMessageRepository.getUserCnt(debate.getRoomId());
            liveResponseDtoList.add(new LiveResponseDto(debate, prosUser.get(), consUser.get(), userCnt));
        }

        return ResponseEntity.ok().body(liveResponseDtoList);
    }
}
