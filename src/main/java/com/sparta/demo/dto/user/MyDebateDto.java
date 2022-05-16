package com.sparta.demo.dto.user;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.DebateVote;
import lombok.Data;

import java.util.List;

@Data
public class MyDebateDto {
    private List<Debate> myDebateList;
    private DebateVote debateVote;
}
