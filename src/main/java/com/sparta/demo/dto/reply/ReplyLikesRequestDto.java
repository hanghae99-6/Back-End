package com.sparta.demo.dto.reply;

import lombok.Data;

@Data
public class ReplyLikesRequestDto {
    private Long replyId;
    private int status;
}
