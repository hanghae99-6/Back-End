package com.sparta.demo.repository;

import com.sparta.demo.model.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Slf4j
@Repository
public class TimerRepository {
    private static final String TIMER = "TIMER";

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private HashOperations<String, String, String> hashOpsEnterInfo;
    private HashOperations<String, String, Object> opsHashChatMessage;
    private ValueOperations<String, String> valueOps;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
        hashOpsEnterInfo = redisTemplate.opsForHash();
        valueOps = stringRedisTemplate.opsForValue();
    }

    public Timer save(Timer timer, String roomId) {
        log.info("timer : {}", timer.getDebateEndTime());
        log.info("type: {}", timer.getType());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Timer.class));

        opsHashChatMessage.put(TIMER, roomId, timer);

        return timer;
    }

    public Object findAll(String roomId){
        return opsHashChatMessage.get(TIMER,roomId);
    }
}