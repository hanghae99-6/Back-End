package com.sparta.demo.controller;

import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.Likes;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.LikesRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.service.LikesService;
import com.sparta.demo.util.GetIp;
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
import java.util.Optional;

@Slf4j
@Controller
@Api(value = "댓글 좋아요 관리 API", tags = {"Like"})
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;
    private final ReplyRepository replyRepository;
    private final LikesRepository likesRepository;

    @PostMapping("/main/reply/likes")
    public ResponseEntity<List<ReplyResponseDto>> getLikes(@RequestBody ReplyLikesRequestDto replyLikesRequestDto,
                                                           HttpServletRequest request){
        String ip = GetIp.getIp(request);
        log.info("getLikes IP : {}", ip);
        log.info("replyLikesRequestDto.getReplyId() : {}", replyLikesRequestDto.getReplyId());
        Reply reply = replyRepository.findByReplyId(replyLikesRequestDto.getReplyId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 댓글입니다."));

        Optional<Likes> found = likesRepository.findByReply_ReplyIdAndIp(replyLikesRequestDto.getReplyId(),ip);
        if(found.isPresent()) {
            return likesService.updateLikes(replyLikesRequestDto, reply, found.get(), request);
        }
        return likesService.createLikes(replyLikesRequestDto, reply, ip,request);
    }
}
