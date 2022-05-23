package com.sparta.demo.dto.debate;

import lombok.Getter;

import java.util.List;

@Getter
public class DebateInfoDto {
    private String prosCons;
    private String opinion;
    private List<String> evidences;
}
