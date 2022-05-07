package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Reply;
import lombok.Data;

@Data
public class ReplyResponseDto {
    private Long replyId;
    private String reply;
    private Long debateId;
    private Long likesCnt;
    private String nickName;

    public ReplyResponseDto(Reply newReply) {
        this.replyId = newReply.getReplyId();
        this.reply = newReply.getReply();
        this.debateId = newReply.getDebate().getDebateId();
        this.nickName = newReply.getUser().getNickName();
    }
}
