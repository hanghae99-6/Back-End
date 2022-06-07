package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import com.sparta.demo.validator.ErrorResult;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    // 토론방 생성
    @PostMapping("/link")
    public ResponseEntity<DebateLinkResponseDto> createLink(
            @RequestBody DebateLinkRequestDto debateLinkRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("userDetails.toString(): {}", userDetails.toString());
        return debateService.createLink(debateLinkRequestDto, userDetails);
    }

//    // 토론방 내에서 필요한 내용
//    @GetMapping("/{roomId}")
//    public ResponseEntity<DebateRoomResponseDto> getRoom(@PathVariable String roomId){
//        return debateService.getRoom(roomId);
//    }
//
//    @GetMapping("/{roomId}/check")
//    public ResponseEntity<DebateRoomIdUserValidateDto> checkRoomIdUser(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
//        log.info("userDetails.getUser() : {}", userDetails.getUser());
//        log.info("userDetails.getUser().getEmail() : {}", userDetails.getUser().getEmail());
//        return debateService.checkRoomIdUser(roomId, userDetails.getUser());
//    }

    // 토론방 퇴장시 주장한 의견과 근거 작성
    @ApiOperation(value = "토론 근거 작성하기")
    @PostMapping("/{roomId}")
    public ResponseEntity<ErrorResult> saveDebateInfo(@PathVariable String roomId,
                                                      @RequestBody DebateInfoDto debateInfoDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("evidence : {}", debateInfoDto.getEvidences().get(0));
        return debateService.saveDebateInfo(roomId, debateInfoDto, userDetails);
    }

    // 타이머 - 토론 시작하기
    @GetMapping("/{roomId}/start-timer")
    public ResponseEntity<DebateTimerRes> startDebateTimer(@PathVariable String roomId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return debateService.startDebateTimer(roomId, userDetails);
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
