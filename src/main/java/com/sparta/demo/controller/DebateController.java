package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import com.sparta.demo.validator.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/{roomId}")
    public ResponseEntity<ErrorResult> saveDebateInfo(@PathVariable String roomId,
                                                      @RequestBody DebateInfoDto debateInfoDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("evidence : {}", debateInfoDto.getEvidences().get(0));
        return debateService.saveDebateInfo(roomId, debateInfoDto, userDetails);
    }

    @PostMapping("/emailCheck/pros")
    public ResponseEntity<ErrorResult> prosEmailCheck(@RequestParam String email){
        log.info("email: {}", email);
        return debateService.emailCheck(email);
    }

    @PostMapping("/emailCheck/cons")
    public ResponseEntity<ErrorResult> consEmailCheck(@RequestParam String email){
        log.info("email: {}", email);
        return debateService.emailCheck(email);
    }
}
