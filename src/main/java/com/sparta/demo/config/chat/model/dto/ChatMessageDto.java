package com.sparta.demo.config.chat.model.dto;

import com.sparta.demo.config.chat.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String sender;
    private String message;
    private String createdAt;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt();
    }
}
