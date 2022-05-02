package com.sparta.demo.model;

import lombok.*;


import javax.persistence.*;

@Getter
@Setter
@Entity
public class StpMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long stompMsgId;

    private MessageType type; // 메시지 타입

    @Column(nullable = false)
    private String roomId; // 방번호

    @Column
    private String videoUser; // 메시지 보낸사람

    @Column
    private String alarm; // 메시지

    @Column
    private long userCount; // 채팅방 인원수, 채팅방 내에서 메시지가 전달될때 인원수 갱신시 사용

    public StpMessage() {

    }

    @Builder
    public StpMessage(MessageType type, String roomId, String videoUser, Long userCount, String alarm) {
        this.type = type;
        this.roomId = roomId;
        this.videoUser = videoUser;
        this.userCount = userCount;
        this.alarm = alarm;
    }

    // 메시지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT
    }

}
