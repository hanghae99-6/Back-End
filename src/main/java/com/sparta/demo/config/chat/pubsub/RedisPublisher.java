package com.sparta.demo.config.chat.pubsub;

import com.sparta.demo.config.chat.model.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisPublisher {

    private final RedisTemplate<String, ChatMessageDto> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessageDto messageDto) {
        if(topic != null) {
            log.info("redisPublisher.topic : {}", topic.getTopic());
        }
        if(messageDto != null) {
            log.info("redisPublisher.messageDto : {}", messageDto.getMessage());
        }
        redisTemplate.convertAndSend(topic.getTopic(), messageDto);
    }
}
