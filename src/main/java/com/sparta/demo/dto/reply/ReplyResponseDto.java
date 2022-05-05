package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Reply;
import lombok.Data;

@Data
public class ReplyResponseDto {
    private Long replyId;
    private String reply;
    private Long debateId;
    private Long likesCnt;

    public ReplyResponseDto(Reply newReply) {
        this.replyId = newReply.getReplyId();
        this.reply = newReply.getReply();
        this.debateId = newReply.getDebate().getDebateId();
    }
//    private User user;
}
