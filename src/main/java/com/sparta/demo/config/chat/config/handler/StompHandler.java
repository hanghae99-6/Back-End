package com.sparta.demo.config.chat.config.handler;

import com.sparta.demo.config.chat.repository.ChatMessageRepository;
import com.sparta.demo.config.chat.service.ChatMessageService;
import com.sparta.demo.config.chat.service.ChatService;
import com.sparta.demo.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("simpDestination : {}", message.getHeaders().get("simpDestination"));

        String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null) {
                token = token.substring(7);
            }
            System.out.println("StompHandler token = " + token);
            jwtDecoder.isValidToken(token);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            chatMessageRepository.plusUserCnt(roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            chatMessageRepository.minusUserCnt(roomId);
            if(chatMessageRepository.getUserCnt(roomId) == 0) {
                chatMessageRepository.delete(roomId);
            }
        }
        return message;
    }
}
