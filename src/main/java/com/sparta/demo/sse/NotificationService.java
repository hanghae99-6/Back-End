package com.sparta.demo.sse;

import com.sparta.demo.model.Debate;
import com.sparta.demo.redis.chat.model.dto.TimerResponseDto;
import com.sparta.demo.repository.DebateRepository;
import com.sparta.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final DebateRepository debateRepository;


    public SseEmitter subscribe(String roomId, String lastEventId) {
        // 1
        String id = roomId + "_" + System.currentTimeMillis();
        log.info("구독 id: {}",id);

        // 2
        SseEmitter emitter = emitterRepository.save(roomId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("구독 emitter timeout: {}",emitter.getTimeout());

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [roomId=" + roomId + "]");
        log.info("더미 이벤트 발송");

        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(roomId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
            log.info("클라이언트가 미수신 목록이 존재할 경우 재전송");
        }

        return emitter;
    }

    // 5
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        log.info("쎈드투클라이언트 진입!");
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
            log.info("클라이언트에게 전송!");
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }

//    @Transactional
//    public Long create(LoginMember loginMember, ReviewRequest reviewRequest) {
//        // ...
//        notificationService.send(teacher, savedReview, "새로운 리뷰 요청이 도착했습니다!");
//
//        return savedReview.getId();
//    }

    public void timer(String roomId, UserDetailsImpl userDetails){
        log.info("타이머 서비스 진입!");
        SseEmitter emitter = emitterRepository.findByRoomId(roomId);
        log.info("emmiter 찾아온 것 : {}", emitter.getTimeout());

        Optional<Debate> debate = debateRepository.findByRoomId(roomId);
        if(debate.get().getUser().getEmail().equals(userDetails.getUser().getEmail())){
            LocalDateTime localDateTime = LocalDateTime.now();
            Long debateTime = debate.get().getDebateTime();
            String debateEndTime = localDateTime.plusMinutes(debateTime).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Boolean isStarted = true;
            TimerResponseDto timerResponseDto = new TimerResponseDto(isStarted,debateEndTime);
            log.info("토론 종료 시간 결과: {}", debateEndTime);
            log.info("timer method emmiter: {}:",emitter.toString());
            log.info("timer method roomId: {}:",roomId);
            log.info("timer method timerResponseDto: {}:",timerResponseDto.getDebateEndTime());
            sendToClient(emitter,roomId,timerResponseDto);
        } else throw new IllegalArgumentException("방장이 아닙니다.");

    }
}