package com.sparta.demo.model;

import com.sparta.demo.dto.TimerResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Timer {
    public enum MessageType {
        ENTER, TIMER, START
    }

    private MessageType type; // 메시지 타입
//    private String roomId; // 방번호
    private String debateEndTime;
    private Boolean isStarted;


    public Timer(TimerResponseDto timerResponseDto){
        this.type = timerResponseDto.getType();
        this.debateEndTime = timerResponseDto.getDebateEndTime();
        this.isStarted = timerResponseDto.getIsStarted();
    }
}
