package com.sparta.demo.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
public enum CategoryEnum {
    All("전체"),
    POLITICS("정치"),
    ECONOMY("경제"),
    SOCIETY("사회"),
    DAILY("일상"),
    CULTURE("문화예술"),
    GLOBAL("해외토픽"),
    SCIENCE("IT과학"),
    ETC("기타");

    final private String name;

    public String getName() {
        return this.name;
    }

    public static CategoryEnum nameOf(String name) {
        for (CategoryEnum categoryEnum : CategoryEnum.values()) {
            if (categoryEnum.getName().equals(name)) {
                return categoryEnum;
            }
        }
        return null;
    }
}
