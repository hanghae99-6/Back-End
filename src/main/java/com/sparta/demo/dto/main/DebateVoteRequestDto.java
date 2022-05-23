package com.sparta.demo.dto.main;

import lombok.Data;

@Data
public class DebateVoteRequestDto {
    private Long debateId;
    private int side;
}
