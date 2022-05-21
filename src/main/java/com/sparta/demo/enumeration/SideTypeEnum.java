package com.sparta.demo.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SideTypeEnum {
    DEFAULT(0, "SIDE"),
    PROS(1, "SIDE"),
    CONS(2, "SIDE");

    private final Integer typeNum;
    private final String name;

}
