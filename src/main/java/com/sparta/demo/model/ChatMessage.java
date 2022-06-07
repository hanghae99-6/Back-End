package com.sparta.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.demo.dto.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessage {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK, QUIT, TIMER, START
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람 nickname
    private String userImage;
    private String message; // 메시지
    private String enterUserCnt;
    private String debateEndTime;
    private Boolean isStarted;

    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createdAt;

    public ChatMessage(){

    }

    public ChatMessage(ChatMessageDto dto){
        this.type = dto.getType();
        this.roomId = dto.getRoomId();
        this.message = dto.getMessage();
    }
}
