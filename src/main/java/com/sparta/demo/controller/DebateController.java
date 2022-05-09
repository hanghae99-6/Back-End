package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// todo: 그냥 컨트롤러 쓰는 건 약간 영문을 모르겠네요?
@Controller
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    @PostMapping("/link")
    public ResponseEntity<DebateLinkResponseDto> createLink(
            @RequestBody DebateLinkRequestDto debateLinkRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return debateService.createLink(debateLinkRequestDto, userDetails);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<DebateRoomResponseDto> getRoom(@PathVariable String roomId){
        return debateService.getRoom(roomId);
    }

    @PostMapping("/check")
    public ResponseEntity<DebateRoomValidateDto> checkRoomIdUser(@RequestBody DebateRoomIdUserCheckDto debateRoomIdUserCheckDto){
        return debateService.checkRoomIdUser(debateRoomIdUserCheckDto);
    }
}
