package com.sparta.demo.dto.main;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import lombok.Data;

import java.util.List;

@Data
public class MainDetailResponseDto {
    private Debate debate;
    private List<EnterUser> enterUserList;

    public MainDetailResponseDto(Debate debate, List<EnterUser> enterUserList) {
        this.debate = debate;
        this.enterUserList = enterUserList;
    }
}
