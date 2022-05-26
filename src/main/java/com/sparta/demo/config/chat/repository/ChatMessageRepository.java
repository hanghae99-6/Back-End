package com.sparta.demo.config.chat.repository;

import com.sparta.demo.config.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepository {// Redis
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private final RedisTemplate<String, ChatMessage> redisTemplate;

    public void save(ChatMessage chatMessage) {
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        redisTemplate.opsForList().rightPush(chatMessage.getDebateId(), chatMessage);
    }

    public List<ChatMessage> findAllMessage(String roomId) {
        List<ChatMessage> chatMessageList = null;
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        if (Boolean.TRUE.equals(redisTemplate.hasKey(roomId))) {
            // 저장된 전체 레코드 수
            chatMessageList = redisTemplate.opsForList().range(roomId, 0, -1);

            if (chatMessageList == null) {
                chatMessageList = new LinkedList<>();
            }
        }

        return chatMessageList;
    }
}
