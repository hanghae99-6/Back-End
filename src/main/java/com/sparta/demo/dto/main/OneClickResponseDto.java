package com.sparta.demo.dto.main;

import com.sparta.demo.model.OneClick;
import lombok.Getter;

@Getter
public class OneClickResponseDto {
    private final Long oneClickId;
    private final String oneClickTopic;
    private final int agreeNum;
    private final int oppoNum;
    private final int oneClickState;

    public OneClickResponseDto(OneClick oneClick, int oneClickState) {
        this.oneClickId = oneClick.getOneClickId();
        this.oneClickTopic = oneClick.getOneClickTopic();
        this.oneClickState = oneClickState;
        this.agreeNum = oneClick.getAgreeNum();
        this.oppoNum = oneClick.getOppoNum();
    }
}
