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

    public MainDetailResponseDto(Debate debate, Long totalPros, Long totalCons) {
        this.debate = debate;
        this.totalCons = totalCons;
        this.totalPros = totalPros;
    }
}
