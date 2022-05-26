package com.sparta.demo.config.chat.controller;

import com.sparta.demo.config.chat.model.ChatMessage;
import com.sparta.demo.config.chat.model.dto.ChatMessageDto;
import com.sparta.demo.config.chat.pubsub.RedisPublisher;
import com.sparta.demo.config.chat.repository.ChatRoomRepository;
import com.sparta.demo.config.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;

    /*
        websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        Date date = new Date();
        log.info("date = : {}", date);
        message.setCreatedAt(date.toString().substring(11,19));
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getDebateId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        } else {
            chatMessageService.save(message);
        }
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getDebateId()), new ChatMessageDto(message));
    }
}
