package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyLikesRequestDto;
import com.sparta.demo.dto.reply.ReplyLikesResponseDto;
import com.sparta.demo.model.Likes;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.LikesRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final ReplyRepository replyRepository;
    private final GetIp getIp;

    @Transactional
    public ResponseEntity<ReplyLikesResponseDto> getLikes(ReplyLikesRequestDto replyLikesRequestDto, HttpServletRequest request) {
        String ip = getIp.getIp(request);
        Reply reply = replyRepository.findByReplyId(replyLikesRequestDto.getReplyId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 댓글입니다."));

        Optional<Likes> found = likesRepository.findByReply_ReplyIdAndIp(replyLikesRequestDto.getReplyId(),ip);

        Long badCnt;
        Long likesCnt;

        if(found.isPresent()){
            if(found.get().getStatus() == replyLikesRequestDto.getStatus()){
                found.get().setStatus(0);
            }else{
                found.get().setStatus(replyLikesRequestDto.getStatus());
            }
            badCnt = likesRepository.countAllByStatus(2);
            likesCnt = likesRepository.countAllByStatus(1);
            found.get().setBadCnt(badCnt);
            found.get().setLikesCnt(likesCnt);
            return ResponseEntity.ok().body(new ReplyLikesResponseDto(found));
        }else {
            Likes likes = new Likes(replyLikesRequestDto, ip, reply);
            likesRepository.save(likes);
            badCnt = likesRepository.countAllByStatus(2);
            likesCnt = likesRepository.countAllByStatus(1);
            likes.setBadCnt(badCnt);
            likes.setLikesCnt(likesCnt);
            return ResponseEntity.ok().body(new ReplyLikesResponseDto(Optional.of(likes)));
        }

    }

}