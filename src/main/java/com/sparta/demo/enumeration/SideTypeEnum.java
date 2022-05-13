package com.sparta.demo.enumeration;

import lombok.Getter;

@Getter
public enum SideTypeEnum {
    PROS(1),
    CONS(2),
    DEFAULT(0);

    private final Integer typeNum;

    SideTypeEnum(Integer typeNum){
        this.typeNum = typeNum;
    }
}
