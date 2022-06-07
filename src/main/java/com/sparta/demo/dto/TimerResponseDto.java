package com.sparta.demo.dto;

import com.sparta.demo.model.Timer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimerResponseDto {
    private Timer.MessageType type; // 메시지 타입
    private Boolean isStarted;
    private String debateEndTime;

//    public TimerResponseDto()
}
