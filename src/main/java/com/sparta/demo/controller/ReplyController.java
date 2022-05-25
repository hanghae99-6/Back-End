package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.ReplyService;
import com.sparta.demo.validator.ErrorResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 리뷰 생성
    @PostMapping("/main/{debateId}/reply")
    public ResponseEntity<List<ReplyResponseDto>> writeReply(@PathVariable Long debateId,
                                                             @RequestBody ReplyRequestDto replyRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             HttpServletRequest request){
        log.info("controller debateId: {}", debateId);
        log.info("controller userDetails.getUsername : {}", userDetails.getUsername());
        return replyService.writeReply(debateId, replyRequestDto, userDetails, request);
    }

    // 리뷰 조회
    @GetMapping("/main/{debateId}/reply")
    public ResponseEntity<List<ReplyResponseDto>> writeReply(@PathVariable Long debateId, HttpServletRequest request){
        log.info("controller debateId: {}", debateId);
        return replyService.getReply(debateId, request);
    }

    // 리뷰 수정
    @PutMapping("/main/reply/{replyId}")
    public ResponseEntity<ErrorResult> updateReply(@RequestBody ReplyRequestDto replyRequestDto,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @PathVariable Long replyId){
        return replyService.updateReply(replyRequestDto, userDetails, replyId);
    }

    // 리뷰 삭제
    @DeleteMapping("/main/reply/{replyId}")
    public ResponseEntity<ErrorResult> deleteReply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @PathVariable Long replyId){
        return replyService.deleteReply(userDetails, replyId);
    }

}