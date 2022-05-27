package com.sparta.demo.dto.debate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class DebateInfoDto {

    private String prosCons; // agree or oppo
    private String opinion;
    private List<String> evidences;

}
