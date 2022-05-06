package com.sparta.demo.dto.main;

import com.sparta.demo.model.DebateVote;
import lombok.Data;

import java.util.Optional;

@Data
public class DebateVoteResponseDto {
    private int side;
    private String ip;
    private Long totalPros;
    private Long totalCons;

    public DebateVoteResponseDto(Optional<DebateVote> found, Long totalCons, Long totalPros) {
        this.side = found.get().getSide();
        this.ip = found.get().getIp();
        this.totalCons = totalCons;
        this.totalPros = totalPros;
    }
}
