package com.sparta.demo.redis.chat.service;

import com.sparta.demo.redis.chat.exception.CustomException;
import com.sparta.demo.redis.chat.model.ChatMessage;
import com.sparta.demo.redis.chat.model.dto.ChatMessageDto;
import com.sparta.demo.redis.chat.pubsub.RedisPublisher;
import com.sparta.demo.redis.chat.repository.ChatMessageRepository;
import com.sparta.demo.redis.chat.repository.ChatRoomRepository;
import com.sparta.demo.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.sparta.demo.redis.chat.exception.ErrorCode.NO_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtDecoder jwtDecoder;


    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String userNickname = "";
        String sender = "";

        String enterUserCnt = valueOperations.get(USER_COUNT + "_" + messageDto.getRoomId());

        // TODO: trim() 쓴 이유
        if(messageDto.getMessage().trim().equals("") && messageDto.getType()!= ChatMessage.MessageType.ENTER){
            throw new CustomException(NO_MESSAGE);
        }

        if (!(String.valueOf(token).equals("Authorization") || String.valueOf(token).equals("null"))) {
            String tokenInfo = token.substring(7); // Bearer 빼고
            userNickname = jwtDecoder.decodeNickName(tokenInfo);
            sender = jwtDecoder.decodeEmail(tokenInfo);
        }



        ChatMessage message = new ChatMessage(messageDto);

        message.setSender(sender);
        message.setNickname(userNickname);

        // 시간 세팅
        Date date = new Date();
        message.setCreatedAt(date);
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setEnterUserCnt(enterUserCnt);
            message.setMessage(message.getNickname() + "님이 입장하셨습니다.");
        } else {
            chatMessageRepository.save(message);
        }
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

    // redis 에 저장되어 있는 message 출력
    public List<ChatMessage> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }
}