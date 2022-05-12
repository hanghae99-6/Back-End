package com.sparta.demo.service;

import com.sparta.demo.dto.main.DebateVoteRequestDto;
import com.sparta.demo.dto.main.DebateVoteResponseDto;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateVote;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.DebateVoteRepository;
import com.sparta.demo.util.GetIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class DebateVoteService {

    private final DebateRepository debateRepository;
    private final DebateVoteRepository debateVoteRepository;
    private final GetIp getIp;
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();

    @Autowired
    public DebateVoteService(DebateRepository debateRepository, DebateVoteRepository debateVoteRepository, GetIp getIp) {
        this.debateRepository = debateRepository;
        this.debateVoteRepository = debateVoteRepository;
        this.getIp = getIp;

        sideTypeEnumMap.put(1,SideTypeEnum.PROS);
        sideTypeEnumMap.put(2,SideTypeEnum.CONS);
        sideTypeEnumMap.put(0,SideTypeEnum.DEFAULT);
    }

    @Transactional
    public ResponseEntity<DebateVoteResponseDto> getVote(DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request) {
        String ip = getIp.getIp(request);
        Debate debate = debateRepository.findByDebateId(debateVoteRequestDto.getDebateId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        Optional<DebateVote> found = debateVoteRepository.findByDebate_DebateIdAndIp(debateVoteRequestDto.getDebateId(),ip);

        SideTypeEnum side = SideTypeEnum.valueOf(String.valueOf(sideTypeEnumMap.get(debateVoteRequestDto.getSide())));
        System.out.println("side: "+side);

        Long totalCons = 0L;
        Long totalPros = 0L;

        if(found.isPresent()){
            if(found.get().getSide() == side){
                found.get().setSide(SideTypeEnum.DEFAULT);
            }else{
                found.get().setSide(side);
            }
            totalCons = debateVoteRepository.countAllBySide(SideTypeEnum.CONS);
            totalPros = debateVoteRepository.countAllBySide(SideTypeEnum.PROS);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(found, totalCons, totalPros));
        }else {
            DebateVote debateVote = new DebateVote(debate,ip, side);
            debateVoteRepository.save(debateVote);
            totalCons = debateVoteRepository.countAllBySide(SideTypeEnum.CONS);
            totalPros = debateVoteRepository.countAllBySide(SideTypeEnum.PROS);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(Optional.of(debateVote),totalCons,totalPros));
        }
    }
}
