//package com.sparta.demo.config.socket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.var;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.socket.WebSocketMessage;
//
//@RequiredArgsConstructor
//public class CustomWebSocketService {
//    private final StringRedisTemplate redisTemplate;
//    private final ObjectMapper mapper;
//
//    public void convertAndSend(String topic, Object message) {
//        var socketData = new WebSocketMessage(topic, message);
//        String data = mapper.writeValueAsString(socketData);
//        redisTemplate.convertAndSend("<channel-name>", data);
//    }
//}
