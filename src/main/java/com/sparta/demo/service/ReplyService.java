package com.sparta.demo.service;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final DebateRepository debateRepository;

    public ResponseEntity<ReplyResponseDto> writeReply(Long debateId, String reply, UserDetailsImpl userDetails) {

        log.info("service debateId: {}",debateId);
        log.info("service reply: {}",reply);

        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        log.info("service debate.getTopic: {}",debate.getTopic());

//        Reply newReply = new Reply(reply,debate);
        // TODO: user와 합치면 생성자에 user을 넣어야함
        Reply newReply = new Reply(reply,debate,userDetails.getUser());

        replyRepository.save(newReply);

        ReplyResponseDto replyResponseDto = new ReplyResponseDto(newReply);

        log.info("service replyResponseDto.getReply(): {}",replyResponseDto.getReply());

        return ResponseEntity.ok().body(replyResponseDto);
    }
}
