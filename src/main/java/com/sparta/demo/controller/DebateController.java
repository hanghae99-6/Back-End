package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    @PostMapping("/link")
    public ResponseEntity<DebateLinkResponseDto> createLink(
            @RequestBody DebateLinkRequestDto debateLinkRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("userDetails.toString(): {}", userDetails.toString());
        return debateService.createLink(debateLinkRequestDto, userDetails);
    }

    // 토론방 내에서 필요한 내용
    @GetMapping("/{roomId}")
    public ResponseEntity<DebateRoomResponseDto> getRoom(@PathVariable String roomId){
        return debateService.getRoom(roomId);
    }

    @GetMapping("/{roomId}/check")
    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("userDetails.getUser() : {}", userDetails.getUser());
        log.info("userDetails.getUser().getEmail() : {}", userDetails.getUser().getEmail());
        return debateService.checkRoomIdUser(roomId, userDetails.getUser());
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<Boolean> saveDebateInfo(@PathVariable String roomId,
                                                  @RequestBody DebateInfoDto debateInfoDto,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails
                                                 ) {
        log.info("evidence : {}", debateInfoDto.getEvidences().get(0));
        return debateService.saveDebateInfo(roomId, debateInfoDto, userDetails);
    }
}
