package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/{debateId}/reply")
//    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId, @RequestBody Map<String, String> param) {
        // TODO: user과 합치면 userdetails에서 유저정보 받아와야함
    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId,@RequestBody Map<String, String> param, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("controller debateId: {}", debateId);
        log.info("controller reply : {}", param.get("reply"));
        return replyService.writeReply(debateId, param.get("reply"), userDetails);
//        return replyService.writeReply(debateId, param.get("reply"));
    }

}
