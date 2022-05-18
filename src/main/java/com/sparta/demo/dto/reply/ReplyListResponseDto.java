package com.sparta.demo.dto.reply;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sparta.demo.model.Reply;
import lombok.Data;

import java.util.List;

@Data
public class ReplyListResponseDto {
    private List<Reply> replyList;

    public ReplyListResponseDto(List<Reply> replyList){
        this.replyList = replyList;
    }
}
