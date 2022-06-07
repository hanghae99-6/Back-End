package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.service.LikesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@Api(value = "댓글 좋아요 관리 API", tags = {"Like"})
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/main/reply/likes")
    @ApiOperation(value = "좋아요 실행", notes = "<strong>좋아요 실행</strong> 좋아요 토글 버튼 클릭 시")
    public ResponseEntity<List<ReplyResponseDto>> getLikes(@RequestBody ReplyLikesRequestDto replyLikesRequestDto,
                                                           HttpServletRequest request){
        return likesService.getLikes(replyLikesRequestDto,request);
    }
}
