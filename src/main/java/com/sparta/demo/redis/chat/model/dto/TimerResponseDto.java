package com.sparta.demo.redis.chat.model.dto;

import com.sparta.demo.redis.chat.model.ChatMessage;
import lombok.Data;

@Data
public class TimerResponseDto {
    private ChatMessage.MessageType type; // 메시지 타입
    private Boolean isStarted;
    private String debateEndTime;
}
