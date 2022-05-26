package com.sparta.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.enumeration.SideTypeEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Reply;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@RequiredArgsConstructor
public class MyReplyDto{
    private Long debateId;
    private String reply;
    private String topic;
    private CategoryEnum categoryEnum;
    private String content;
    private Long likesCnt;
    private Long badCnt;
    private SideTypeEnum side;
    private LocalDateTime createdAt;


    public MyReplyDto(Reply reply, Debate debate){
        this.debateId = debate.getDebateId();
        this.reply = reply.getReply();
        this.topic = debate.getTopic();
        this.categoryEnum = debate.getCategoryEnum();
        this.content = debate.getContent();
        this.likesCnt = reply.getLikesCnt();
        this.badCnt = reply.getBadCnt();
        this.side = reply.getSide();
        this.createdAt = reply.getCreatedAt();
    }
}
