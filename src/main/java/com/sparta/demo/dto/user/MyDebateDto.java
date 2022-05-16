package com.sparta.demo.dto.user;

import com.sparta.demo.model.Debate;
import lombok.Data;

@Data
public class MyDebateDto {
    private Debate debate;
    private Long totalPros;
    private Long totalCons;
    private int totalReply;

    public MyDebateDto(Debate debate, Long totalPros, Long totalCons, int totalReply) {
        this.debate = debate;
        this.totalCons = totalCons;
        this.totalPros = totalPros;
        this.totalReply = totalReply;
    }
}
