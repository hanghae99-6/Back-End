package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import lombok.Data;

import java.util.List;

@Data
public class MainResponseDto {
    private List<Debate> mainDebateList;
}