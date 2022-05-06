package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Likes;
import lombok.Data;

import java.util.Optional;


@Data

public class ReplyLikesResponseDto {
//    private Long likesId;
//    private int status;
//    private String ip;
//    private Long replyId;
    Optional<Likes> likes;

    public ReplyLikesResponseDto(Optional<Likes> likes){
//        this.likesId = likes.get().getLikesId();
//        this.status = likes.get().getStatus();
//        this.ip = likes.get().getIp();
//        this.replyId = likes.get().getReply().getReplyId();
        this.likes = likes;
    }

}
