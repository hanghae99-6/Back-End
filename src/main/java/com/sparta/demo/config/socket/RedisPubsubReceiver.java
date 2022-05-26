//package com.sparta.demo.config.socket;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.var;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.socket.WebSocketMessage;
//
//@RequiredArgsConstructor
//public class RedisPubsubReceiver {
//
//    private final SimpMessagingTemplate template;
//    private final ObjectMapper mapper;
//
//    public void receiveMessage(String message) throws JsonProcessingException {
//        var data = mapper.readValue(message, WebSocketMessage.class);
//        template.convertAndSend(data.getTopic(), data.getMessage());
//    }
//
//}
