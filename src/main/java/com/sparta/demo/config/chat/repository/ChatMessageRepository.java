package com.sparta.demo.config.chat.repository;

import com.sparta.demo.config.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChatMessageRepository {// Redis
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, List<ChatMessage>> opsHashChatMessage;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
    }

    public ChatMessage save(ChatMessage chatMessage) {
        log.info("chatMessage : {}", chatMessage.getMessage());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        String roomId = chatMessage.getRoomId();
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);
        if (chatMessageList == null) chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessage);

        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);

        return chatMessage;
    }

    public List<ChatMessage> findAllMessage(String roomId) {
        log.info("findAllMessage");
        return opsHashChatMessage.get(CHAT_MESSAGE, roomId);
        }
    }
