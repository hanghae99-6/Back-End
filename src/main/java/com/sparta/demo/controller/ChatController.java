package com.sparta.demo.controller;

import com.sparta.demo.model.ChatMessage;
import com.sparta.demo.dto.ChatMessageDto;
import com.sparta.demo.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Api(value = "채팅 관리 API", tags = {"Chatting"})
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @ApiOperation(value = "채팅 발행하기")
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("요청 메서드 [Message] /chat/message");
        chatService.save(message, token);
    }

    // 이전 채팅 기록 가져오기
    @ApiOperation(value = "채팅 기록 가져오기")
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        log.info("요청 메서드 [GET] /chat/message/{roomId}");
        return chatService.getMessages(roomId);
    }
}
