package com.sparta.demo.dto.debate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebateTimerRes {
    private String debateStartTime;
    private String debateEndTime;

}
