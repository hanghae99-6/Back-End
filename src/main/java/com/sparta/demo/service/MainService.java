package com.sparta.demo.service;

import com.sparta.demo.dto.main.MainDetailResponseDto;
import com.sparta.demo.dto.main.MainResponseDto;
import com.sparta.demo.dto.main.OneClickRequestDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.OneClick;
import com.sparta.demo.model.Reply;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.repository.OneClickRepository;
import com.sparta.demo.repository.ReplyRepository;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    final private DebateRepository debateRepository;
    final private ReplyRepository replyRepository;
    final private DebateVoteRepository debateVoteRepository;
    private final OneClickRepository oneClickRepository;

    public ResponseEntity<List<OneClick>> getOneClick() {
        List<OneClick> oneClicks = oneClickRepository.findAll();
        return ResponseEntity.ok().body(oneClicks);
    }

    public ResponseEntity<MainResponseDto> getMain(){
        Pageable pageable = Pageable.ofSize(4);

        Page<Debate> debateList = debateRepository.findAllByOrderByCreatedAtDesc(pageable);

        MainResponseDto mainResponseDto = new MainResponseDto(debateList);

        return ResponseEntity.ok().body(mainResponseDto);
    }

    public ResponseEntity<MainResponseDto> getCatMain(String catName){
        Pageable pageable = Pageable.ofSize(4);

        log.info("service catName: {}", catName);
        Page<Debate> debateList = debateRepository.findAllByCatNameContains(catName, pageable);

        MainResponseDto mainResponseDto = new MainResponseDto(debateList);


        return ResponseEntity.ok().body(mainResponseDto);
    }


    public ResponseEntity<MainDetailResponseDto> getMainDetail(Long debateId) {
        log.info("service debateId: {}", debateId);
        Debate debate = debateRepository.findByDebateId(debateId).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        List<Reply> replyList = replyRepository.findAllByDebate_DebateId(debateId);

        Long totalCons = debateVoteRepository.countAllBySide(2);
        Long totalPros = debateVoteRepository.countAllBySide(1);

        MainDetailResponseDto mainDetailResponseDto = new MainDetailResponseDto(debate, replyList, totalPros,totalCons);

        log.info("debate.getTopic: {}", debate.getTopic());
        return ResponseEntity.ok().body(mainDetailResponseDto);
    }

    public ResponseEntity<OneClick> sumOneClick(OneClickRequestDto oneClickRequestDto, HttpServletRequest request) {
        String userIp = GetIp.getIp(request);
        int side = oneClickRequestDto.getSide();
        String oneClickTopic = oneClickRequestDto.getOneClickTopic();

        Optional<OneClick> optionalOneClick = oneClickRepository.findByUserIpAndOneClickTopic(userIp, oneClickTopic);
        if(!optionalOneClick.isPresent()){
            throw new IllegalArgumentException("이미 선택한 토픽입니다.");
        }

        return null;
    }
}

