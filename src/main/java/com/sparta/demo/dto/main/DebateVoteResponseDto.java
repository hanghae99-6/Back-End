package com.sparta.demo.dto.main;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateVote;
import lombok.Data;

import java.util.Optional;

@Data
public class DebateVoteResponseDto {
    private SideTypeEnum side;
    private String ip;
    private Long totalPros;
    private Long totalCons;

    public DebateVoteResponseDto(Optional<DebateVote> found, Debate debate) {
        this.side = found.get().getSide();
        this.ip = found.get().getIp();
        this.totalCons = debate.getTotalCons();
        this.totalPros = debate.getTotalPros();
    }
}
