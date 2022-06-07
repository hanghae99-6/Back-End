package com.sparta.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {

    @Value("${serialNum}")
    private static Long serialNum;

    private static final long serialVersionUID = serialNum;

    private String debateId;
    private String topic;

    public static ChatRoom create(String topic) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.debateId = UUID.randomUUID().toString();
        chatRoom.topic = topic;
        return chatRoom;
    }
}
