package com.sparta.demo.service;

import com.sparta.demo.dto.debate.DebateLinkRequestDto;
import com.sparta.demo.dto.debate.DebateLinkResponseDto;
import com.sparta.demo.dto.debate.DebateRoomResponseDto;
import com.sparta.demo.model.Debate;
import com.sparta.demo.model.StpMessage;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.repository.StpMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebateService {

    private final DebateRepository debateRepository;
    private final StpMessageRepository stpMessageRepository;


    public ResponseEntity<DebateLinkResponseDto> createLink(DebateLinkRequestDto debateLinkRequestDto) {
        Debate debate = Debate.create(debateLinkRequestDto);
        Debate newDebate = debateRepository.save(debate);

        DebateLinkResponseDto debateLinkResponseDto = new DebateLinkResponseDto();
        debateLinkResponseDto.setRoomId(newDebate.getRoomId());

        return ResponseEntity.ok().body(debateLinkResponseDto);
    }

    public ResponseEntity<DebateRoomResponseDto> getRoom(String roomId) {
        Debate debate = debateRepository.findByRoomId(roomId).orElseThrow(()->new NullPointerException("존재하지 않는 방입니다."));
        return ResponseEntity.ok().body(new DebateRoomResponseDto(debate));
    }





    // StompHandler에서 필요한 메서드 모음(삭제하거나 이동 예정)
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    public void sendChatMessage(StpMessage stpMessage) throws InterruptedException {

        stpMessage.setUserCount(stpMessageRepository.findAllByRoomId(stpMessage.getRoomId())+1);

        if (StpMessage.MessageType.ENTER.equals(stpMessage.getType())) {
            Thread.sleep(2000);
            stpMessage.setAlarm(stpMessage.getVideoUser() + "님이 방에 입장했습니다.");
            stpMessage.setVideoUser("[알림]");

        }
        if (StpMessage.MessageType.QUIT.equals(stpMessage.getType())) {

            stpMessage.setAlarm(stpMessage.getVideoUser() + "님이 방에서 나갔습니다.");
            stpMessage.setVideoUser("[알림]");

        }
//
//        //유튜브 URL을 제출한다면 WorkOut true(유저가 방에 진입하지 못함, 운동중)
//        if(ChatMessage.MessageType.YOUTUBEURL.equals(chatMessage.getType())) {
//            Room room = roomRepository.findByroomId(chatMessage.getRoomId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
//            room.setWorkOut(true);
//            String[] arr = StringUtil.split(room.getRoomImg(), '/');
//            //유저가 직접 지정한 방 이미지가 아니라면 유튜브 썸네일을 방 이미지로 저장
//            if(!arr[3].equals("static")) {
//                String Thumbnail = "https://img.youtube.com/vi/" + chatMessage.getMessage().substring(chatMessage.getMessage().lastIndexOf("/") + 1) + "/mqdefault.jpg";
//                String result = Thumbnail.replace("watch?v=", "");
//                room.setRoomImg(result);
//            }
//            roomRepository.save(room);
//        }
//
//        //유튜브 영상이 끝났을 때 WorkOut false(유저가 방에 진입 가능, 휴식중)
//        if(ChatMessage.MessageType.YOUTUBESTOP.equals(chatMessage.getType())) {
//            Room room = roomRepository.findByroomId(chatMessage.getRoomId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
//            room.setWorkOut(false);
//            roomRepository.save(room);
//        }
//
//        //유튜브 영상을 정지하였을 때 WorkOut false(유저가 방에 진입 가능, 휴식중)
//        if(ChatMessage.MessageType.YOUTUBEPAUSE.equals(chatMessage.getType())) {
//            Room room = roomRepository.findByroomId(chatMessage.getRoomId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
//            room.setWorkOut(false);
//            roomRepository.save(room);
//        }
//
//        if(ChatMessage.MessageType.VIDEOON.equals(chatMessage.getType())) {
//            ChatMessage.builder().type(ChatMessage.MessageType.ENTER);
//        }
//
//        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
