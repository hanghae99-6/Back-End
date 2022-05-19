package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.service.ReplyService;
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

    @PostMapping("/main/{debateId}/reply")
    public ResponseEntity<List<ReplyResponseDto>> writeReply(@PathVariable Long debateId,
                                                             @RequestBody Map<String, String> param,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             HttpServletRequest request){
        log.info("controller debateId: {}", debateId);
        log.info("controller reply : {}", param.get("reply"));
        log.info("controller userDetails.getUsername : {}", userDetails.getUsername());
        return replyService.writeReply(debateId, param.get("reply"), userDetails, request);
    }

    @GetMapping("/main/{debateId}/reply")
    public ResponseEntity<List<ReplyResponseDto>> writeReply(@PathVariable Long debateId, HttpServletRequest request){
        log.info("controller debateId: {}", debateId);
        return replyService.getReply(debateId, request);
    }

}
