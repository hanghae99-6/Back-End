package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyListResponseDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.LikesRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final DebateRepository debateRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> writeReply(Long debateId, String reply, UserDetailsImpl userDetails, HttpServletRequest request) {

        log.info("service debateId: {}",debateId);
        log.info("service reply: {}",reply);
        log.info("service userDetails.getUsername: {}",userDetails.getUsername());

        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));
        
        Reply newReply = new Reply(reply,debate,userDetails.getUser());
        replyRepository.save(newReply);

        debate.setTotalReply(debate.getTotalReply() + 1);

        List<ReplyResponseDto> replyResponseDtoList = getReply(debateId, request).getBody();

        return ResponseEntity.ok().body(replyResponseDtoList);
    }

    public ResponseEntity<List<ReplyResponseDto>> getReply(Long debateId, HttpServletRequest request) {
        log.info("service debateId: {}",debateId);

        String ip = GetIp.getIp(request);
        System.out.println(ip);

        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));
        List<Reply> replyList = debate.getReplyList();
        List<ReplyResponseDto> replyResponseDtoList = new ArrayList<>();
        for (Reply reply: replyList){
            Integer status = likesRepository.getStatusByReplyIdAndIp(reply.getReplyId(), ip);
            log.info("service status: {}", status);
            if(status == null){
                status = 0;
            }
            ReplyResponseDto replyResponseDto = new ReplyResponseDto(reply, status);
            replyResponseDtoList.add(replyResponseDto);
        }

        return ResponseEntity.ok().body(replyResponseDtoList);
    }
}
