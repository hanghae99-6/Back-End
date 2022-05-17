package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Reply;
import lombok.Data;

@Data
public class ReplyResponseDto {
    private Reply reply;

    public ReplyResponseDto(Reply newReply) {
        this.reply = newReply;
    }
}
