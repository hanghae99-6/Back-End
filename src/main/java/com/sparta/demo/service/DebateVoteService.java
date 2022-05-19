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
    private final Map<Integer, SideTypeEnum> sideTypeEnumMap = new HashMap<>();

    @Autowired
    public DebateVoteService(DebateRepository debateRepository, DebateVoteRepository debateVoteRepository, GetIp getIp) {
        this.debateRepository = debateRepository;
        this.debateVoteRepository = debateVoteRepository;

        sideTypeEnumMap.put(1,SideTypeEnum.PROS);
        sideTypeEnumMap.put(2,SideTypeEnum.CONS);
        sideTypeEnumMap.put(0,SideTypeEnum.DEFAULT);
    }

    @Transactional
    public ResponseEntity<DebateVoteResponseDto> getVote(DebateVoteRequestDto debateVoteRequestDto, HttpServletRequest request) {
        String ip = GetIp.getIp(request);
        Debate debate = debateRepository.findByDebateId(debateVoteRequestDto.getDebateId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 토론입니다."));

        Optional<DebateVote> found = debateVoteRepository.findByDebate_DebateIdAndIp(debateVoteRequestDto.getDebateId(),ip);

        SideTypeEnum side = sideTypeEnumMap.get(debateVoteRequestDto.getSide());
        System.out.println("side: "+side);

        if(found.isPresent()){
            if(found.get().getSide() == side){
                found.get().setSide(SideTypeEnum.DEFAULT);
                debateVoteRepository.delete(found.get());
            }else{
                found.get().setSide(side);
            }
            setTotals(debate);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(found, debate));
        }else {
            DebateVote debateVote = new DebateVote(debate,ip, side);
            debateVoteRepository.save(debateVote);
            setTotals(debate);
            return ResponseEntity.ok().body(new DebateVoteResponseDto(Optional.of(debateVote),debate));
        }

        // todo: 상세페이지에 들어갔을 때 Ip 주소에 따라서 추천여부(side) 확인해서 내려줘야하는 부분 추가
    }


    // 찬반 총 투표수 debate에 저장
    // todo: + - 계산으로 변경하는 방안 고민
    private void setTotals(Debate debate) {
        Long totalCons;
        Long totalPros;
        totalCons = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.CONS, debate.getDebateId());
        totalPros = debateVoteRepository.countAllBySideAndDebate_DebateId(SideTypeEnum.PROS, debate.getDebateId());
        debate.setTotalCons(totalCons);
        debate.setTotalPros(totalPros);
    }
}
