package com.sparta.demo.dto.debate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebateLinkRequestDto {
    private String topic;
    private String categoryName;
    private String prosName;
    private String consName;
//    private int speechCnt;
//    private int speechMinute;
    private String content;
}
