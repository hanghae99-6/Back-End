package com.sparta.demo.redis.chat.pubsub;

import com.sparta.demo.redis.chat.model.ChatMessage;
import com.sparta.demo.redis.chat.model.Timer;
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

    public void publish(ChannelTopic topic, Timer timer) {
        log.info("Timer ChannelTopic : {}", topic.getTopic());
        log.info("Timer ChatMessage : {}", timer.getType());
        redisTemplate.convertAndSend(topic.getTopic(), timer);
        System.out.println("발행 완료");
    }
}
