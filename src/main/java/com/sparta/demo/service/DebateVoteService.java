package com.sparta.demo.service;

import com.sparta.demo.dto.main.DebateVoteRequestDto;
import com.sparta.demo.dto.main.DebateVoteResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateVote;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.util.GetIp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebateVoteService {

    private final DebateRepository debateRepository;
    private final DebateVoteRepository debateVoteRepository;
    private final GetIp getIp;

    @Transactional
    public ResponseEntity<DebateVoteResponseDto> getVote(DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request) {
        String ip = getIp.getIp(request);
        Debate debate = debateRepository.findByDebateId(debateVoteRequestDto.getDebateId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        Optional<DebateVote> found = debateVoteRepository.findByDebate_DebateIdAndIp(debateVoteRequestDto.getDebateId(),ip);

        Long totalCons = 0L;
        Long totalPros = 0L;

        if(found.isPresent()){
            if(found.get().getSide() == debateVoteRequestDto.getSide()){
                found.get().setSide(0);
            }else{
                found.get().setSide(debateVoteRequestDto.getSide());
            }
            totalCons = debateVoteRepository.countAllBySide(2);
            totalPros = debateVoteRepository.countAllBySide(1);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(found, totalCons, totalPros));
        }else {
            DebateVote debateVote = new DebateVote(debate,ip,debateVoteRequestDto.getSide());
            debateVoteRepository.save(debateVote);
            totalCons = debateVoteRepository.countAllBySide(2);
            totalPros = debateVoteRepository.countAllBySide(1);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(Optional.of(debateVote),totalCons,totalPros));
        }
    }
}
