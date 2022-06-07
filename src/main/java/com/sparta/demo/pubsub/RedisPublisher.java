package com.sparta.demo.pubsub;

import com.sparta.demo.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessage message) {
        log.info("ChannelTopic : {}", topic.getTopic());
        log.info("ChatMessage : {}", message.getType());
        redisTemplate.convertAndSend(topic.getTopic(), message);
        System.out.println("발행 완료!");
    }
}
