package com.sparta.demo.dto.reply;

import com.sparta.demo.model.Likes;
import lombok.Data;

import java.util.Optional;


@Data

public class ReplyLikesResponseDto {
    Optional<Likes> likes;

    public ReplyLikesResponseDto(Optional<Likes> likes){
        this.likes = likes;
    }

}
