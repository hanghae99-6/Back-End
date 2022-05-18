package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Reply;
import lombok.Data;

import java.util.List;

@Data
public class ReplyResponseDto {
    private List<Reply> replyList;

    public ReplyResponseDto(List<Reply> replyList) {
        this.replyList = replyList;
    }
}
