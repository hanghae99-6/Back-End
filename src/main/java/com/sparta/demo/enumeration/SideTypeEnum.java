package com.sparta.demo.enumeration;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SideTypeEnum {
    DEFAULT(0, "무"),
    PROS(1, "찬성"),
    CONS(2, "반대");

    private final Integer typeNum;
    private final String name;

    public String getName() {
        return this.name;
    }

    public int getTypeNum() {
        return this.typeNum;
    }

    public static SideTypeEnum typeOf(int typeNum) {
        for (SideTypeEnum status : SideTypeEnum.values()) {
            if (status.getTypeNum() == typeNum) {
                return status;
            }
        }
        return null;
    }

    public static SideTypeEnum nameOf(String name) {
        for (SideTypeEnum status : SideTypeEnum.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }

}
