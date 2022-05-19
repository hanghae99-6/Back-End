package com.sparta.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class SseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    // 여러 비동기 작업의 진행률을 추적하고 종료되는 작업들을 관리하는 객체를 생성할 수 있는 실행자
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // try catch 문을 실행하고 종료 (<-> shutdownNow() 메소드는 바로 종료)
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }

    @GetMapping("/time")
    @CrossOrigin
    public SseEmitter streamDateTime() {
        // SseEmitter 인스턴스가 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

        sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

        executor.execute(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    sseEmitter.send(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
                    sleep(1, sseEmitter);
                } catch (IOException e) {
                    e.printStackTrace();
                    sseEmitter.completeWithError(e);
                }
            }
            sseEmitter.complete();
        });

        LOGGER.info("Controller exits");
        return sseEmitter;
    }

    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sseEmitter.completeWithError(e);
        }
    }

}
