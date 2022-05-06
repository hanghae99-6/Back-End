//package com.sparta.demo.controller;
//
//import com.sparta.demo.dto.reply.ReplyResponseDto;
//import com.sparta.demo.service.ReplyService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//public class ReplyController {
//
//    private final ReplyService replyService;
//
//    @PostMapping("/reply/{debateId}")
//    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId, @RequestParam String reply){
//        // TODO: user과 합치면 userdetails에서 유저정보 받아와야함
////    public ResponseEntity<ReplyResponseDto> writeReply(@PathVariable Long debateId, @RequestParam String reply, @AuthenticationPrincipal UserDetailsImpl userDetails){
////        return replyService.writeReply(debateId, userDetails);
//        return replyService.writeReply(debateId, reply);
//    }
//
//}
