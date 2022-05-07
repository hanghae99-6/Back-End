package com.sparta.demo.controller;

import com.sparta.demo.dto.main.DebateVoteRequestDto;
import com.sparta.demo.dto.main.DebateVoteResponseDto;
import com.sparta.demo.service.DebateVoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DebateVoteController {
    private final DebateVoteService debateVoteService;

    @PostMapping("/main/debate/vote")
    public ResponseEntity<DebateVoteResponseDto> getVote(@RequestBody DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request){
        log.info("debateId {}: ",debateVoteRequestDto.getDebateId());
        log.info("side {}: ",debateVoteRequestDto.getSide());
        return debateVoteService.getVote(debateVoteRequestDto, request);
    }
}
