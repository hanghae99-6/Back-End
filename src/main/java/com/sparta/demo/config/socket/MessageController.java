//package com.sparta.demo.config.socket;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.listener.Topic;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class MessageController {
//
//    private final SimpMessageSendingOperations simpMessageSendingOperations;
//    private final CustomWebSocketService service;
//
//    /*
//        /sub/channel/12345      - 구독(channelId:12345)
//        /pub/hello              - 메시지 발행
//    */
//
//    @MessageMapping("/hello") // 클라이언트에서 /pub/hello 로 메세지를 발행
//    public void message(Topic topic, Message message) {
//        // 메세지에 정의된 채널 id 에 메세지를 보낸다.
//        // /sub/channel/채널아이디 에 구독 중인 클라이언트에서 메세지를 보낸다.
//        simpMessageSendingOperations.convertAndSend("/sub/channel/" + message.getChannelId(), message);
//        service.convertAndSend(topic, message);
//    }
//}
