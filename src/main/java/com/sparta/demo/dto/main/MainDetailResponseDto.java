package com.sparta.demo.dto.main;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.EnterUser;
import lombok.Data;

import java.util.List;

@Data
public class MainDetailResponseDto {
    private Debate debate;
    private List<EnterUser> enterUserList;
    private SideTypeEnum side;

    public MainDetailResponseDto(Debate debate, List<EnterUser> enterUserList, SideTypeEnum side) {
        this.debate = debate;
        this.enterUserList = enterUserList;
        this.side = side;
    }
}
