package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.dto.debate.DebateRoomResponseDto;
import com.sparta.demo.dto.debate.DebateRoomValidateDto;
import com.sparta.demo.service.DebateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// todo: 그냥 컨트롤러 쓰는 건 약간 영문을 모르겠네요?
@Controller
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    @PostMapping("/link")
    public ResponseEntity<DebateLinkResponseDto> createLink(@RequestBody DebateLinkRequestDto debateLinkRequestDto){
        return debateService.createLink(debateLinkRequestDto);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<DebateRoomResponseDto> getRoom(@PathVariable String roomId){
        return debateService.getRoom(roomId);
    }

    @GetMapping("/{roomId}/check")
    public ResponseEntity<DebateRoomValidateDto> validateRoomId(@PathVariable String roomId){
        return debateService.validateRoomId(roomId);
    }
}
