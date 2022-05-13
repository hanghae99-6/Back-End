package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class MainResponseDto {
//    private Page<Debate> mainDebateList;
    private List<Debate> mainDebateList;

    public MainResponseDto(List<Debate>debateList){
        this.mainDebateList = debateList;
    }
}