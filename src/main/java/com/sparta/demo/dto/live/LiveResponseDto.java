package com.sparta.demo.dto.live;

import com.sparta.demo.model.Debate;
import lombok.Data;

@Data
public class LiveResponseDto {
    private Long debateId;
    private String roomId;
    private String topic;
    private String content;
    private int enterUserCnt;
    private String status;

    // todo: 나중에 실시간 투표 추가기능이 되면...?
//    private int prosNow;
//    private int consNow;

    public LiveResponseDto(Debate debate) {
        this.debateId = debate.getDebateId();
        this.roomId = debate.getRoomId();
        this.topic = debate.getTopic();
        this.content = debate.getContent();
        // todo: 추후에 현재 채팅방에 있는 인원으로 변경해야함
        this.enterUserCnt = debate.getEnterUserList().size();
        this.status = debate.getStatusEnum().getName();
    }
}
