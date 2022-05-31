package com.sparta.demo.redis.chat.model.dto;

import com.sparta.demo.redis.chat.model.ChatMessage;
import com.sparta.demo.redis.chat.model.Timer;
import lombok.Data;

@Data
public class TimerResponseDto {
    private Timer.MessageType type; // 메시지 타입
    private Boolean isStarted;
    private String debateEndTime;
}
