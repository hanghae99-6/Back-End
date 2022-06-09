package com.sparta.demo.sse;


import com.sparta.demo.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Api(value = "SSE", tags = {"SSE"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

    private final NotificationService notificationService;


    /**
     * @title 로그인 한 유저 sse 연결
     * 페이지 시작했을 때 구독 요청
     */
    @ApiOperation(value = "토로방 구독", notes = "토로방 구독")
    @GetMapping(value = "/subscribe/{roomId}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String roomId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        log.info("구독 controller 입장");
        return notificationService.subscribe(roomId, lastEventId);
    }

    // 토론 시작하기 눌렀을 때
    @ApiOperation(value = "토로방 시작하기 타이머", notes = "토로방 시작하기 타이머")
    @GetMapping("/timer/{roomId}")
    public void sseTimer(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        log.info("타이머 controller 입장");
        notificationService.timer(roomId, userDetails);
    }
}