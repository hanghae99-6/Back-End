package com.sparta.demo.dto.user;

import com.sparta.demo.model.Debate;
import lombok.Data;

@Data
public class MyDebateDto {
    private Debate debate;
    private int totalReply;
    private int side;

    public MyDebateDto(Debate debate, int totalReply, int side) {
        this.debate = debate;
        this.totalReply = totalReply; // 내가 참여한 토론에 작성된 총 댓글 수
        this.side = side;
    }
}
