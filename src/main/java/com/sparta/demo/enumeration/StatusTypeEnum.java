package com.sparta.demo.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusTypeEnum {
    HOLD("대기중"),
    LIVEON("진행중"),
    LIVEOFF("종료");

    private final String name;

    public String getName(){
        return this.name;
    }

    public static StatusTypeEnum nameOf(String name){
        for (StatusTypeEnum statusEnum: StatusTypeEnum.values()) {
            if(statusEnum.getName().equals(name)){
                return statusEnum;
            }
        }
        return null;
    }
}
