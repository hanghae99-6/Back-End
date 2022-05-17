package com.sparta.demo.dto.user;

import com.sparta.demo.enumeration.CategoryEnum;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Likes;
import com.sparta.demo.model.Reply;

import javax.swing.*;
import java.util.List;

public class MyReplyDto {
    private String reply;
    private String topic;
    private CategoryEnum categoryEnum;
    private String content;
    private List<Likes> likesList;

    public MyReplyDto(String reply, List<Likes> likesList, String topic, CategoryEnum categoryEnum, String content) {
        this.reply = reply;
        this.likesList = likesList;
        this.topic = topic;
        this.categoryEnum = categoryEnum;
        this.content = content;
    }
}