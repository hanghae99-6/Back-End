package com.sparta.demo.dto;

import com.sparta.demo.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    // 메시지 타입 : 입장, 채팅
    private ChatMessage.MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String message; // 메시지
}
