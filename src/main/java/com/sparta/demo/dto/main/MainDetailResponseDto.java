package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import lombok.Data;

import java.util.List;

@Data
public class MainDetailResponseDto {
    private Debate debate;
    private Long totalPros;
    private Long totalCons;
    private List<Reply> replyList;

    public MainDetailResponseDto(Debate debate, List<Reply> replyList, Long totalPros, Long totalCons) {
        this.debate = debate;
        this.replyList = replyList;
        this.totalCons = totalCons;
        this.totalPros = totalPros;
    }
}
