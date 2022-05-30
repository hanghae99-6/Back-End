package com.sparta.demo.dto.live;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.User;
import lombok.Data;

@Data
public class LiveResponseDto {
    private Long debateId;
    private String roomId;
    private String topic;
    private String content;
    private Long enterUserCnt;
    private String status;
    private String category;

    private String prosNickName;
    private String prosImage;
    private String consNickName;
    private String consImage;

    // todo: 나중에 실시간 투표 추가기능이 되면...?
//    private int prosNow;
//    private int consNow;

    public LiveResponseDto(Debate debate, User prosUser, User consUser, Long userCnt) {
        this.debateId = debate.getDebateId();
        this.roomId = debate.getRoomId();
        this.topic = debate.getTopic();
        this.content = debate.getContent();
        // todo: 추후에 현재 채팅방에 있는 인원으로 변경해야함
        this.enterUserCnt = userCnt;
        this.status = debate.getStatusEnum().getName();
        this.category = debate.getCategoryEnum().getName();

        this.prosNickName = prosUser.getNickName();
        this.prosImage = prosUser.getProfileImg();
        this.consNickName = consUser.getNickName();
        this.consImage = consUser.getProfileImg();
    }
}
