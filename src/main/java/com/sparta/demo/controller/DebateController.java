package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.model.Debate;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// todo: 그냥 컨트롤러 쓰는 건 약간 영문을 모르겠네요?
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    @PostMapping("/link")
    public ResponseEntity<DebateLinkResponseDto> createLink(
            @RequestBody DebateLinkRequestDto debateLinkRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

//        return debateService.createLink(debateLinkRequestDto, userDetails);
        return debateService.createLink(debateLinkRequestDto);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<DebateRoomResponseDto> getRoom(@PathVariable String roomId){
        return debateService.getRoom(roomId);
    }
    @GetMapping("/{roomId}/check/{username}")
    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(@PathVariable String roomId, @PathVariable String username){
        return debateService.checkRoomIdUser(roomId, username);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<String> saveDebateInfo(@PathVariable String roomId,
                                                 @RequestBody DebateInfoDto debateInfoDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return debateService.saveDebateInfo(roomId, debateInfoDto, userDetails);
    }
}
