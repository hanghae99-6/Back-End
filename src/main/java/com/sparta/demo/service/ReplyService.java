package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.ReplyRepository;
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

//    @Transactional
    public ResponseEntity<ReplyResponseDto> writeReply(Long debateId, String reply) {

        log.info("service debateId: {}",debateId);
        log.info("service reply: {}",reply);

        Debate debate = debateRepository.findByDebateId(debateId);

        log.info("service debate.getTopic: {}",debate.getTopic());

        Reply newReply = new Reply(reply,debate);
        // TODO: user와 합치면 생성자에 user을 넣어야함
//        Reply newReply = new Reply(reply,debate,user);

        replyRepository.save(newReply);

        ReplyResponseDto replyResponseDto = new ReplyResponseDto();
        replyResponseDto.setReply(newReply);

        log.info("service replyResponseDto.getReply().getReply(): {}",replyResponseDto.getReply().getReply());

        return ResponseEntity.ok().body(replyResponseDto);
    }
}
