package com.sparta.demo.config.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    public NotificationService(EmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    public SseEmitter subscribe(Long roomId, String lastEventId) {

        // 1
        // 데이터의 id 값을 ${userId}_${System.currentTimeMillis()} 형태로 두면 데이터가 유실된 시점을
        // 파악할 수 있으므로 저장된 key 값 비교를 통해 유실된 데이터만 재전송 할 수 있게 된다.
        String id = roomId + "_" + System.currentTimeMillis();
        log.info("subscribe key : {}", id);

        // 2
        // id를 key 로, SseEmitter 를 value 로 저장해둔다.
        // 그리고 SseEmitter 의 시간 초과 및 네트워크 오류를 포함한 모든 이유로 비동기 요청이 정상 동작할 수 없다면
        // 저장해둔 SseEmitter 를 삭제한다.
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

        // 3
        // 연결 요청에 의해 SseEmitter 가 생성되면 더미 데이터를 보내줘야한다.
        // sse 연결이 이뤄진 후, 하나의 데이터도 전송되지 않는다면 SseEmitter 의 유효 시간이 끝나는 순간
        // 503 응답이 발생하는 문제가 있다. 따라서 연결시 바로 더미 데이터를 한 번 보내준다.
        sendToClient(emitter, id, "EventStream Created. [userId=" + roomId + "]");

        // 4
        // 1번 부분과 관련이 있는 부분이다.
        // Last-Event-ID 값이 헤더에 있는 경우, 저장된 데이터 캐시에서 id 값과 Last-Event-ID 값을 통해
        // 유실된 데이터들만 다시 보내준다.
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(roomId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    // 3
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id);
            throw new RuntimeException("연결 오류!");
        }
    }

//    public void send(Member receiver, Review review, String content) {
//        Notification notification = createNotification(receiver, review, content);
//        String id = String.valueOf(receiver.getId());
//
//        // 로그인 한 유저의 SseEmitter 모두 가져오기
//        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
//        sseEmitters.forEach(
//                (key, emitter) -> {
//                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
//                    emitterRepository.saveEventCache(key, notification);
//                    // 데이터 전송
//                    sendToClient(emitter, key, NotificationResponse.from(notification));
//                }
//        );
//    }
//
//    private Notification createNotification(Member receiver, Review review, String content) {
//        return Notification.builder()
//                .receiver(receiver)
//                .content(content)
//                .review(review)
//                .url("/reviews/" + review.getId())
//                .isRead(false)
//                .build();
//    }

//    private void sendToClient(SseEmitter emitter, String id, Object data) {
//        try {
//            emitter.send(SseEmitter.event()
//                    .id(id)
//                    .name("sse")
//                    .data(data));
//        } catch (IOException exception) {
//            emitterRepository.deleteById(id);
//            throw new RuntimeException("연결 오류!");
//        }
//    }
}
