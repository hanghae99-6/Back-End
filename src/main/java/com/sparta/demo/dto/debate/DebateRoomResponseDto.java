package com.sparta.demo.dto.debate;

import com.sparta.demo.model.Debate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebateRoomResponseDto {

    private Long debateId;
    private String topic;
    private String prosName;
    private String consName;
    private int speechCnt;
    private int speechMinute;
    private String content;

    public DebateRoomResponseDto(Debate debate) {
        this.debateId = debate.getDebateId();
        this.topic = debate.getTopic();
        this.prosName = debate.getProsName();
        this.consName = debate.getConsName();
        this.speechCnt = debate.getSpeechCnt();
        this.speechMinute = debate.getSpeechMinute();
        this.content = debate.getContent();
    }
}
