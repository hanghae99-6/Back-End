package com.sparta.demo.dto.debate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebateLinkResponseDto {

    private String roomId;

    public DebateLinkResponseDto(String roomId) {
        this.roomId = roomId;
    }
}
