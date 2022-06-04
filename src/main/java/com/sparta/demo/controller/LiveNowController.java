package com.sparta.demo.controller;

import com.sparta.demo.dto.live.LiveResponseDto;
import com.sparta.demo.service.LiveNowService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/live")
public class LiveNowController {

    private final LiveNowService liveNowService;

    @GetMapping("")
    @ApiOperation(value = "LiveNow 보여주기", notes = "현재 라이브 중인 토론방 목록 확인")
    public ResponseEntity<List<LiveResponseDto>> getLiveNow(){
        return liveNowService.getLiveNow();
    }
}
