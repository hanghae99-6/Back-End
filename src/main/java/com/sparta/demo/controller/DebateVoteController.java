package com.sparta.demo.controller;

import com.sparta.demo.dto.main.DebateVoteRequestDto;
import com.sparta.demo.dto.main.DebateVoteResponseDto;
import com.sparta.demo.service.DebateVoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@Api(value = "상세페이지 찬반 관리 API", tags = {"Debate Vote"})
@RequiredArgsConstructor
public class DebateVoteController {
    private final DebateVoteService debateVoteService;

    @PostMapping("/main/debate/vote")
    @ApiOperation(value = "찬반 투표", notes = "<strong>찬반 투표</strong> 상세페이지 내부의 찬반 투표 관리")
    public ResponseEntity<DebateVoteResponseDto> getVote(@RequestBody DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request){
        log.info("debateId {}: ",debateVoteRequestDto.getDebateId());
        log.info("side {}: ",debateVoteRequestDto.getSide());
        return debateVoteService.getVote(debateVoteRequestDto, request);
    }
}
