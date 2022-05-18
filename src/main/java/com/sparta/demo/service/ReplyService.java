package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyListResponseDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final DebateRepository debateRepository;

    @Transactional
    public ResponseEntity<ReplyResponseDto> writeReply(Long debateId, String reply, UserDetailsImpl userDetails) {

        log.info("service debateId: {}",debateId);
        log.info("service reply: {}",reply);
        log.info("service userDetails.getUsername: {}",userDetails.getUsername());

        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        log.info("service debate.getTopic: {}",debate.getTopic());

        Reply newReply = new Reply(reply,debate,userDetails.getUser());

        replyRepository.save(newReply);

        debate.setTotalReply(debate.getTotalReply() + 1);

        ReplyResponseDto replyResponseDto = new ReplyResponseDto(newReply);

        log.info("service replyResponseDto.getReply(): {}",replyResponseDto.getReply());

        return ResponseEntity.ok().body(replyResponseDto);
    }

    public ResponseEntity<ReplyListResponseDto> getReply(Long debateId) {
        log.info("service debateId: {}",debateId);

        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        return ResponseEntity.ok().body(new ReplyListResponseDto(debate.getReplyList()));
    }
}
