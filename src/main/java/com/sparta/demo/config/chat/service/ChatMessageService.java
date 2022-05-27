package com.sparta.demo.config.chat.service;

import com.sparta.demo.config.chat.model.ChatMessage;
import com.sparta.demo.config.chat.model.dto.ChatMessageDto;
import com.sparta.demo.config.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void save(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDto> getMessages(String debateId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllMessage(debateId);
        List<ChatMessageDto> dtoList = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessageList) {
            ChatMessageDto dto = new ChatMessageDto(chatMessage);
            dtoList.add(dto);
            System.out.println("dto = " + dto);
        }

        return dtoList;
    }
}
