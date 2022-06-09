package com.sparta.demo.sse;

import com.sparta.demo.model.Debate;
import com.sparta.demo.model.Timer;
import com.sparta.demo.dto.TimerResponseDto;
import com.sparta.demo.repository.ChatMessageRepository;
import com.sparta.demo.repository.TimerRepository;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000;

    private final EmitterRepository emitterRepository;
    private final DebateRepository debateRepository;
    private final TimerRepository timerRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final Set<SseEmitter> emitterSet = new CopyOnWriteArraySet<>();

    public SseEmitter subscribe(String roomId, String lastEventId) {
        // 1
        String id = roomId + "_" + System.currentTimeMillis();
        log.info("구독 id: {}", id);

        for (SseEmitter emitter: emitterSet) {
            log.info("emitterSet 안에: {}",emitter.getTimeout());
        }

        // 2
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterSet.add(sseEmitter);
        log.info("Add emitterSet size(): {}",emitterSet.size());

        sseEmitter.onTimeout(() -> emitterSet.remove(sseEmitter));
        sseEmitter.onCompletion(() -> emitterSet.remove(sseEmitter));

        log.info("Remove emitterSet size(): {}",emitterSet.size());

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(id, "EventStream Created. [roomId=" + roomId + "]");
        log.info("더미 이벤트 발송");

        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(roomId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
                    .forEach(entry -> sendToClient(entry.getKey(), entry.getValue()));
            log.info("클라이언트가 미수신 목록이 존재할 경우 재전송");
        }

        // 5 (redis 저장된 값)
        if (timerRepository.findAll(roomId) != null) {
//            sendToClient(emitter, id, timerRepository.findAll(roomId));
            sendToClient(id, timerRepository.findAll(roomId));
            log.info("타이머 레포지토리 진입");
        }

//        return emitter;
        return sseEmitter;
    }

    // 6
//    private void sendToClient(SseEmitter emitter, String id, Object data) {
//        log.info("쎈드투클라이언트 진입!");
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(id)
//                    .data(data));
//            log.info("클라이언트에게 전송!");
//        } catch (IOException exception) {
//            emitterRepository.deleteById(id);
//            throw new RuntimeException("연결 오류!");
//        }
//    }

    @Async
    public void sendToClient(String id, Object data) {
        log.info("쎈드투클라이언트 진입!");
        List<SseEmitter> deadEmitters = new ArrayList<>();
        final int[] i = {1};
        log.info("emitterSet size(): {}", emitterSet.size());
        emitterSet.forEach(emitter -> {
            log.info("emitterSet.forEach: {}번", i[0]);
            i[0]++;
            log.info("emitterSet.forEach emitter 확인: {}",emitter.getTimeout());
            try {
                emitter.send(SseEmitter.event()
                        .id(id)
                        .data(data));
                log.info("클라이언트에게 전송!");
            } catch (Exception ignore) {
                deadEmitters.add(emitter);
                emitter.complete();
                emitterRepository.deleteById(id);
                log.warn("disconnected id : {}", id);
                throw new RuntimeException("연결 오류!");
            }
        });
        deadEmitters.forEach(emitterSet::remove);
    }

    public ResponseEntity<TimerResponseDto> timer(String roomId, UserDetailsImpl userDetails) {
        log.info("타이머 서비스 진입!");

        SseEmitter emitter = emitterRepository.findByRoomId(roomId);
        Set<SseEmitter> emitterList = new CopyOnWriteArraySet<>();
        for (int i = 0; i < emitterSet.size(); i++) {
            emitterList.add(emitter);
        }
        log.info("emmiter 찾아온 것 : {}", emitter.getTimeout());


        Debate debate = debateRepository.findByRoomId(roomId).orElseThrow(
                () -> new IllegalArgumentException("없는 토론방입니다.")
        );

        if (!debate.getUser().getEmail().equals(userDetails.getUser().getEmail())) {
            throw new IllegalArgumentException("방장이 아닙니다.");
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        Long debateTime = debate.getDebateTime();
        log.info("토론할 시간 : {}", debateTime);
        String debateEndTime = localDateTime.plusMinutes(debateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Boolean isStarted = true;
        // redis 에 저장장
        Timer timer = new Timer();
        timer.setType(Timer.MessageType.START);
        timer.setDebateEndTime(debateEndTime);
        timer.setIsStarted(isStarted);
        timerRepository.save(timer, roomId);
        TimerResponseDto timerResponseDto = new TimerResponseDto(Timer.MessageType.START, isStarted, debateEndTime);
        log.info("토론 종료 시간 결과: {}", debateEndTime);
//        log.info("timer method emmiter: {}:", emitter);
        log.info("timer method roomId: {}:", roomId);
        log.info("timer method timerResponseDto: {}:", timerResponseDto.getDebateEndTime());

//        int i = 0;
//        for (; i < emitterSet.size(); i++) {
////            sendToClient(emit, roomId, timerResponseDto);
//            log.info("emitterList for : {}번째", i);
//            sendToClient(roomId, timerResponseDto);
//        }

        sendToClient(roomId, timerResponseDto);

        return ResponseEntity.ok().body(timerResponseDto);
    }
}
