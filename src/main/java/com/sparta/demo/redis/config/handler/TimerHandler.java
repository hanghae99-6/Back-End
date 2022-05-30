package com.sparta.demo.redis.config.handler;

import com.sparta.demo.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TimerHandler implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("30, simpDestination : {}", message.getHeaders().get("simpDestination"));
        log.info("31, sessionId : {}", message.getHeaders().get("simpSessionId"));
        String sessionId = "";
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("TIMER CONNECT : 타이머 커넥트");
            sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("Timer CONNECT Command : {}", sessionId);
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null) {
                token = token.substring(7);
            }
            System.out.println("StompHandler token = " + token);
            jwtDecoder.isValidToken(token);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("Timer SUB Command : {}", sessionId);
            log.info("SUBSCRIBE : {}", sessionId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("Timer DISCONNECT Command : {}", sessionId);
            log.info("DISCONNECT : {}", sessionId);
        }
        return message;
    }
}
