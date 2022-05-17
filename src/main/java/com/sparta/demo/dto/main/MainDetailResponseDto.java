package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import lombok.Data;

import java.util.List;

@Data
public class MainDetailResponseDto {
    private Debate debate;

    public MainDetailResponseDto(Debate debate) {
        this.debate = debate;
    }
}
