package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.Likes;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final ReplyService replyService;

    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> updateLikes(ReplyLikesRequestDto replyLikesRequestDto, Reply reply, Likes found, HttpServletRequest request) {
        if(found.getStatus() == replyLikesRequestDto.getStatus()){
            switch (replyLikesRequestDto.getStatus()){
                case 1: reply.setLikesCnt(reply.getLikesCnt() -1);
                    break;
                case 2: reply.setBadCnt(reply.getBadCnt() -1);
                    break;
            }
            likesRepository.delete(found);
        }else{
            found.setStatus(replyLikesRequestDto.getStatus());
            switch (replyLikesRequestDto.getStatus()){
                case 1: reply.setLikesCnt(reply.getLikesCnt()+1);
                    reply.setBadCnt(reply.getBadCnt() -1);
                    break;
                case 2: reply.setBadCnt(reply.getBadCnt() +1);
                    reply.setLikesCnt(reply.getLikesCnt() -1);
                    break;
            }
        }
        List<ReplyResponseDto> replyResponseDtoList = replyService.getReply(reply.getDebate().getDebateId(), request).getBody();
        return ResponseEntity.ok().body(replyResponseDtoList);

    }

    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> createLikes(ReplyLikesRequestDto replyLikesRequestDto, Reply reply, String ip,  HttpServletRequest request) {

        Likes likes = new Likes(replyLikesRequestDto, ip, reply);
        likesRepository.save(likes);
        switch (replyLikesRequestDto.getStatus()){
            case 1: reply.setLikesCnt(reply.getLikesCnt()+1);
                break;
            case 2: reply.setBadCnt(reply.getBadCnt() +1);
                break;
        }

        List<ReplyResponseDto> replyResponseDtoList = replyService.getReply(reply.getDebate().getDebateId(), request).getBody();
        return ResponseEntity.ok().body(replyResponseDtoList);

    }

}
