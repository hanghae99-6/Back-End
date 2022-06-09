package com.sparta.demo.controller;

import com.sparta.demo.dto.debate.*;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.DebateService;
import com.sparta.demo.validator.ErrorResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(value = "토론 관리 API", tags = {"Debate"})
@RequiredArgsConstructor
@RequestMapping("/debate")
public class DebateController {
    private final DebateService debateService;

    // 토론방 생성
    @PostMapping("/link")
    @ApiOperation(value = "방 만들 때 RoomId 생성", notes = "<strong>방만들기</strong> 방을 생성하고 roomId 프론트로 전송")
    public ResponseEntity<DebateLinkResponseDto> createLink(
            @RequestBody DebateLinkRequestDto debateLinkRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("userDetails.toString(): {}", userDetails.toString());
        return debateService.createLink(debateLinkRequestDto, userDetails);
    }

    // 토론방 퇴장시 주장한 의견과 근거 작성
    @PostMapping("/{roomId}")
    @ApiOperation(value = "토론 근거 작성하기")
    public ResponseEntity<ErrorResult> saveDebateInfo(@PathVariable String roomId,
                                                      @RequestBody DebateInfoDto debateInfoDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("evidence : {}", debateInfoDto.getEvidences().get(0));
        return debateService.saveDebateInfo(roomId, debateInfoDto, userDetails);
    }


    @PostMapping("/emailCheck/pros")
    @ApiOperation(value = "찬성 이메일 유효성 검사", notes = "<strong>찬성 이메일 유효성 검사</strong> 방 생성 시 찬성 측 이메일 유효성검사")
    public ResponseEntity<ErrorResult> prosEmailCheck(@RequestParam String email){
        log.info("email: {}", email);
        return debateService.emailCheck(email);
    }

    @PostMapping("/emailCheck/cons")
    @ApiOperation(value = "반대 이메일 유효성 검사", notes = "<strong>반대 이메일 유효성 검사</strong> 방 생성 시 반대 측 이메일 유효성검사")
    public ResponseEntity<ErrorResult> consEmailCheck(@RequestParam String email){
        log.info("email: {}", email);
        return debateService.emailCheck(email);

    }
}
