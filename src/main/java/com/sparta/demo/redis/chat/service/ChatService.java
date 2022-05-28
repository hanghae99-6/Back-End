package com.sparta.demo.redis.chat.service;

import com.sparta.demo.exception.CustomException;
import com.sparta.demo.redis.chat.model.ChatMessage;
import com.sparta.demo.redis.chat.model.dto.ChatMessageDto;
import com.sparta.demo.redis.chat.pubsub.RedisPublisher;
import com.sparta.demo.redis.chat.repository.ChatMessageRepository;
import com.sparta.demo.redis.chat.repository.ChatRoomRepository;
import com.sparta.demo.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.sparta.demo.exception.ErrorCode.NO_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final JwtDecoder jwtDecoder;


    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        String sender = "";
        String userImage = "";

        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());

        // TODO: trim() 쓴 이유 : 빈 칸 안받으려고
        if(messageDto.getMessage().trim().equals("") && messageDto.getType()!= ChatMessage.MessageType.ENTER){
            throw new CustomException(NO_MESSAGE);
        }

        if (!(String.valueOf(token).equals("Authorization") || String.valueOf(token).equals("null"))) {
            String tokenInfo = token.substring(7); // Bearer 빼고
            sender = jwtDecoder.decodeNickName(tokenInfo);
            userImage = jwtDecoder.decodeImage(tokenInfo);
        }



        ChatMessage message = new ChatMessage(messageDto);

        message.setSender(sender);
        message.setUserImage(userImage);
        message.setEnterUserCnt(String.valueOf(enterUserCnt));
        Date date = new Date();
        message.setCreatedAt(date); // 시간 세팅

        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage("[알림] " + message.getSender() + "님이 입장하셨습니다.");
            message.setSender("\uD83D\uDC51PEECH KING\uD83D\uDC51");
        } else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
            message.setMessage("[알림] " + message.getSender() + "님이 나가셨습니다.");
            message.setSender("\uD83D\uDC51PEECH KING\uD83D\uDC51");
        }

        chatMessageRepository.save(message);
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

    // redis 에 저장되어 있는 message 출력
    public List<ChatMessage> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }
}
