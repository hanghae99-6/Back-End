package com.sparta.demo.redis.chat.model.dto;

import lombok.Data;

@Data
public class TimerResponseDto {
    private Boolean isStarted;
    private String debateEndTime;
}
