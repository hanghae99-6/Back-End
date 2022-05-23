package com.sparta.demo.dto.reply;

import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Reply;
import com.sparta.demo.model.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyResponseDto {
    private User user;
    private Long replyId;
    private String reply;
    private Long badCnt;
    private Long likesCnt;
    private LocalDateTime createdAt;
    private int status;
    private SideTypeEnum side;

    public ReplyResponseDto(Reply newReply,int status){
        this.user = newReply.getUser();
        this.replyId = newReply.getReplyId();
        this.reply = newReply.getReply();
        this.badCnt = newReply.getBadCnt();
        this.likesCnt = newReply.getLikesCnt();
        this.createdAt = newReply.getCreatedAt();
        this.status = status;
        this.side = newReply.getSide();
    }
}
