package com.sparta.demo.service;

import com.sparta.demo.dto.reply.ReplyRequestDto;
import com.sparta.demo.dto.reply.ReplyResponseDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.LikesRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.security.UserDetailsImpl;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final DebateRepository debateRepository;
    private final LikesRepository likesRepository;

    // 댓글 생성
    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> writeReply(Long debateId, ReplyRequestDto replyRequestDto, UserDetailsImpl userDetails, HttpServletRequest request) {

        String reply = replyRequestDto.getReply();
        SideTypeEnum side = SideTypeEnum.typeOf(replyRequestDto.getSide());
        if(side == SideTypeEnum.DEFAULT){
            throw new NullPointerException("찬반 값이 입력되지 않았습니다.");
        }
        log.info("service debateId: {}",debateId);
        log.info("service reply: {}",reply);
        log.info("service userDetails.getUsername: {}",userDetails.getUsername());
        // debateId 유효성 검사
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));
        // reply entity에 저장
        Reply newReply = new Reply(reply,debate,userDetails.getUser(), side);
        replyRepository.save(newReply);
        // debate에 totalReply(댓글 개수) 하나 더하기
        debate.setTotalReply(debate.getTotalReply() + 1);
        // 댓글 전체 보내기 위해 getReply 메소드 사용
        List<ReplyResponseDto> replyResponseDtoList = getReply(debateId, request).getBody();

        return ResponseEntity.ok().body(replyResponseDtoList);
    }

    // 댓글 조회
    public ResponseEntity<List<ReplyResponseDto>> getReply(Long debateId, HttpServletRequest request) {
        log.info("service debateId: {}",debateId);
        // ip 주소 가져오기(utill의 GetIp class의 getIp 메서드 이용)
        String ip = GetIp.getIp(request);
        // debateId 유효성 검사
        debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));
        // 해당 debate에 연관 되어 있는 reply 모두 불러와서 List 형태로 집어넣음
        List<Reply> replyList = replyRepository.findTop60ByDebate_DebateId(Sort.by(Sort.Direction.DESC, "replyId"),debateId);
        // return을 위한 List<ReplyResponseDto> 초기화
        List<ReplyResponseDto> replyResponseDtoList = new ArrayList<>();
        // 반복문을 통해서 위에 불러온 replyList 내의 reply들을 ReplyResponseDto에 집어 넣는 작업
        for (Reply reply: replyList){
            // Like entity에서 ip와 replyId를 통해 status값만 가져 올 수 있는 쿼리문 작성
            Integer status = likesRepository.getStatusByReplyIdAndIp(reply.getReplyId(), ip);
            log.info("service status: {}", status);
            // status를 Integer로 받는 이유: null 값이 나올 수 있기 때문에(int는 null값을 받을 수 없음)
            // null 일 경우 status를 default 값인 0으로 반환
            if(status == null){
                status = 0;
            }
            // reply들을 ReplyResponseDto에 집어 넣는 작업
            ReplyResponseDto replyResponseDto = new ReplyResponseDto(reply, status);

            log.info("getReply Method reply.getLikesCnt : {}", reply.getLikesCnt());
            log.info("getReply Method reply.getBadCnt : {}", reply.getBadCnt());

            // replyResponse를 List 형태로 만드는 작업
            replyResponseDtoList.add(replyResponseDto);
        }

        return ResponseEntity.ok().body(replyResponseDtoList);
    }

    // 댓글 수정
    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> updateReply(ReplyRequestDto replyRequestDto, UserDetailsImpl userDetails, Long replyId, HttpServletRequest request){
        Optional<Reply> reply = replyRepository.findByReplyId(replyId);

        log.info("유저정보: {}", userDetails.getUser().getEmail());
        if(!reply.isPresent()){
            throw new IllegalArgumentException("댓글 정보가 없습니다");
        } else {
            if(reply.get().getUser().getEmail().equals(userDetails.getUser().getEmail())){
                reply.get().updateReply(replyRequestDto);
                log.info("댓글 수정이 완료 되었습니다!");
            }
            List<ReplyResponseDto> replyResponseDtoList = getReply(reply.get().getDebate().getDebateId(), request).getBody();

            return ResponseEntity.ok().body(replyResponseDtoList);
        }

    }

    // 댓글 삭제
    @Transactional
    public ResponseEntity<List<ReplyResponseDto>> deleteReply(UserDetailsImpl userDetails, Long replyId, HttpServletRequest request){
        Optional<Reply> reply = replyRepository.findByReplyId(replyId);

        if(!reply.isPresent()){
            throw new IllegalArgumentException("댓글 정보가 없습니다");
        } else {
            if(reply.get().getUser().getEmail().equals(userDetails.getUser().getEmail())){
                replyRepository.deleteById(replyId);
                reply.get().getDebate().setTotalReply(reply.get().getDebate().getTotalReply() -1);
                log.info("댓글 삭제가 완료되었습니다!");
            }
            List<ReplyResponseDto> replyResponseDtoList = getReply(reply.get().getDebate().getDebateId(), request).getBody();
            return ResponseEntity.ok().body(replyResponseDtoList);
        }
    }
}
