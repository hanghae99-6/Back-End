package com.sparta.demo.dto.user;

import com.sparta.demo.model.Debate;
import lombok.Data;

@Data
public class MyDebateDto {
    private Debate debate;
    private int totalReply;
    private long totalDebateCnt;

    public MyDebateDto(Debate debate, int totalReply, int totalDebateCnt) {
        this.debate = debate;
        this.totalReply = totalReply;
        this.totalDebateCnt = totalDebateCnt;
    }
}
