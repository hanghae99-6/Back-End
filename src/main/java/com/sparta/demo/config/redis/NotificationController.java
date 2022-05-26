package com.sparta.demo.config.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * @title 로그인 한 유저 sse 연결
     */
    @GetMapping(value = "/subscribe/{roomId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long roomId,
                                /*
                                Last-Event-ID 헤더는 클라이언트가 마지막으로 수신한 데이터 id 값
                                이를 이용하여 시간 만료 등의 이유로 SSE 연결이 끊어졌을 경우 유실된 데이터를 다시 보내줄 수 있다.
                                */
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(roomId, lastEventId);
    }
}
