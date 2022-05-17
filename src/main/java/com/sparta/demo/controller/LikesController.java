package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyLikesResponseDto;
import com.sparta.demo.service.LikesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/main/reply/likes")
    public ResponseEntity<ReplyLikesResponseDto> getLikes(@RequestBody ReplyLikesRequestDto replyLikesRequestDto,
                                                          HttpServletRequest request){
        return likesService.getLikes(replyLikesRequestDto,request);
    }
}
