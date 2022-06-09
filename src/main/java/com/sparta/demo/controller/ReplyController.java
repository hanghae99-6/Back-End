package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.ReplyService;
import com.sparta.demo.validator.ErrorResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@Api(value = "댓글 관리 API", tags = {"Reply"})
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    // 리뷰 생성
    @PostMapping("/main/{debateId}/reply")
    @ApiOperation(value = "댓글 생성", notes = "<strong>댓글 생성</strong> 댓글 작성 시")
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
    @ApiOperation(value = "댓글 조회", notes = "<strong>댓글 조회</strong> 상세페이지에서 댓글 조회용 API")
    public ResponseEntity<List<ReplyResponseDto>> writeReply(@PathVariable Long debateId, HttpServletRequest request){
        log.info("controller debateId: {}", debateId);
        return replyService.getReply(debateId, request);
    }

    // 리뷰 수정
    @PutMapping("/main/reply/{replyId}")
    @ApiOperation(value = "댓글 수정", notes = "<strong>댓글 수정</strong> 상세페이지에서 댓글 수정용 API")
    public ResponseEntity<List<ReplyResponseDto>> updateReply(@RequestBody ReplyRequestDto replyRequestDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable Long replyId, HttpServletRequest request){
        return replyService.updateReply(replyRequestDto, userDetails, replyId, request);
    }

    // 리뷰 삭제
    @DeleteMapping("/main/reply/{replyId}")
    @ApiOperation(value = "댓글 삭제", notes = "<strong>댓글 삭제</strong> 상세페이지에서 댓글 삭제용 API")
    public ResponseEntity<List<ReplyResponseDto>> deleteReply(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                   @PathVariable Long replyId,
                                                   HttpServletRequest request){
        return replyService.deleteReply(userDetails, replyId, request);
    }

}